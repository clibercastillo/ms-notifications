package com.utp.ms_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private Long bookingId;
    private String userEmail;
    private Long stadiumId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private String status;
}