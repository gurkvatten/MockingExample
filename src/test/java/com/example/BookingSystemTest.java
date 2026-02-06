package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock TimeProvider timeProvider;
    @Mock RoomRepository roomRepository;
    @Mock NotificationService notificationService;

    @InjectMocks BookingSystem bookingSystem;

    @Test
    void bookRoom_shouldThrow_whenAnyArgumentIsNull() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 3, 13, 0);

        assertThatThrownBy(() -> bookingSystem.bookRoom(null, start, end))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> bookingSystem.bookRoom("R1", null, end))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> bookingSystem.bookRoom("R1", start, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void bookRoom_shouldThrow_whenStartTimeIsInThePast() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.minusMinutes(1);
        LocalDateTime end = now.plusHours(1);

        assertThatThrownBy(() ->
                bookingSystem.bookRoom("R1", start, end)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dåtid");
    }
    @Test
    void bookRoom_shouldThrow_whenEndTimeIsBeforeStartTime() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.plusHours(2);
        LocalDateTime end = now.plusHours(1);

        assertThatThrownBy(() ->
                bookingSystem.bookRoom("R1", start, end)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid");
    }
    @Test
    void bookRoom_shouldThrow_whenRoomDoesNotExist() {

        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);

        when(roomRepository.findById("missing")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() ->
                bookingSystem.bookRoom("missing", start, end)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existerar inte");

        verify(roomRepository).findById("missing");
        verify(roomRepository, never()).save(any());
    }
    @Test
    void bookRoom_shouldReturnFalse_whenRoomIsNotAvailable() throws NotificationException {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);

        Room room = new Room("R1", "Room 1");

        room.addBooking(new Booking(
                "B1",
                "R1",
                start.minusMinutes(10),
                start.plusMinutes(10)
        ));

        when(roomRepository.findById("R1")).thenReturn(java.util.Optional.of(room));

        boolean result = bookingSystem.bookRoom("R1", start, end);


        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }
    @Test
    void bookRoom_shouldSaveRoomAndSendConfirmation_whenRoomIsAvailable() throws Exception {

        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);

        Room room = new Room("R1", "Room 1");
        when(roomRepository.findById("R1")).thenReturn(java.util.Optional.of(room));

        boolean result = bookingSystem.bookRoom("R1", start, end);

        assertThat(result).isTrue();
        verify(roomRepository).save(room);

        var captor = org.mockito.ArgumentCaptor.forClass(Booking.class);
        verify(notificationService).sendBookingConfirmation(captor.capture());

        Booking sentBooking = captor.getValue();
        assertThat(sentBooking.getRoomId()).isEqualTo("R1");
        assertThat(sentBooking.getStartTime()).isEqualTo(start);
        assertThat(sentBooking.getEndTime()).isEqualTo(end);
        assertThat(sentBooking.getId()).isNotBlank();
    }
    @Test
    void bookRoom_shouldStillReturnTrue_whenConfirmationNotificationFails() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);

        Room room = new Room("R1", "Room 1");
        when(roomRepository.findById("R1")).thenReturn(java.util.Optional.of(room));

        doThrow(new NotificationException("fail"))
                .when(notificationService).sendBookingConfirmation(any());

        boolean result = bookingSystem.bookRoom("R1", start, end);

        assertThat(result).isTrue();
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any());
    }
    @Test
    void getAvailableRooms_shouldThrow_whenStartOrEndIsNull() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 3, 13, 0);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(null, end))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(start, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getAvailableRooms_shouldThrow_whenEndIsBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 13, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 3, 12, 0);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid");
    }
    @Test
    void getAvailableRooms_shouldReturnOnlyRoomsThatAreAvailable() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 3, 13, 0);

        Room available = new Room("A", "Available room");

        Room notAvailable = new Room("N", "Not available room");
        notAvailable.addBooking(new Booking(
                "B1",
                "N",
                start.minusMinutes(10),
                start.plusMinutes(10)
        ));

        when(roomRepository.findAll())
                .thenReturn(java.util.List.of(available, notAvailable));

        java.util.List<Room> result =
                bookingSystem.getAvailableRooms(start, end);

        assertThat(result)
                .extracting(Room::getId)
                .containsExactly("A");

        verify(roomRepository).findAll();
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.NullSource
    void cancelBooking_shouldThrow_whenBookingIdIsNull(String bookingId) {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void cancelBooking_shouldReturnFalse_whenBookingIsNotFoundInAnyRoom() throws NotificationException {
        Room room1 = new Room("R1", "Room 1");
        Room room2 = new Room("R2", "Room 2");

        when(roomRepository.findAll())
                .thenReturn(java.util.List.of(room1, room2));

        boolean result = bookingSystem.cancelBooking("missing-booking-id");

        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendCancellationConfirmation(any());
    }
    @Test
    void cancelBooking_shouldThrow_whenBookingHasAlreadyStarted() throws NotificationException {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        Room room = new Room("R1", "Room 1");
        Booking booking = new Booking(
                "B1",
                "R1",
                now.minusMinutes(1),
                now.plusHours(1)
        );
        room.addBooking(booking);

        when(roomRepository.findAll())
                .thenReturn(java.util.List.of(room));

        assertThatThrownBy(() -> bookingSystem.cancelBooking("B1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("avboka");

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendCancellationConfirmation(any());
    }





}
