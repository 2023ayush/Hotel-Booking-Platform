package com.bookingservice.entity;

import com.bookingservice.entity.Bookings;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "booking_date",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"room_id", "date", "username"}
        )
)
public class BookingDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate date;

    private Long roomId;

    private String username;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Bookings bookings;

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bookings getBookings() {
        return bookings;
    }

    public void setBookings(Bookings bookings) {
        this.bookings = bookings;
    }
}
