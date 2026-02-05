package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
}
