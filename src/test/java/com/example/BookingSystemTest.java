package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}
