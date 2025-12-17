package com.properyservice.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RoomAvailabilityDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private int availableRooms;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }
}
