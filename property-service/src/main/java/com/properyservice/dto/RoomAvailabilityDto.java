package com.properyservice.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RoomAvailabilityDto implements Serializable {
    private LocalDate date;
    private int availableRooms;
    private long roomId; // ADD THIS

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }
}
