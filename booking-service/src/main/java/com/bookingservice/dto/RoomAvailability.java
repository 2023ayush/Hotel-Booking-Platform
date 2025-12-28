package com.bookingservice.dto;

import java.time.LocalDate;

public class RoomAvailability {

    private LocalDate date; // match PropertyService
    private int availableRooms; // match PropertyService
    private long roomId; // optional if you get roomId separately

    // Getters & Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }
}
