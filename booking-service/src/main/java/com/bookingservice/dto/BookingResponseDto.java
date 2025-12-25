package com.bookingservice.dto;

import com.bookingservice.entity.BookingStatus;
import java.time.LocalDate;
import java.util.List;

public class BookingResponseDto {
    private Long bookingId;
    private String propertyName;
    private BookingStatus status;
    private double totalPrice;
    private List<LocalDate> dates;

    // Getters & Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<LocalDate> getDates() { return dates; }
    public void setDates(List<LocalDate> dates) { this.dates = dates; }
}
