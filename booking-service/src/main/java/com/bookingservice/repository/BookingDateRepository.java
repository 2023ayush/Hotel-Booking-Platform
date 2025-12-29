package com.bookingservice.repository;

import com.bookingservice.entity.BookingDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingDateRepository extends JpaRepository<BookingDate, Long> {

        boolean existsByRoomIdAndDateAndUsername(Long roomId, LocalDate date, String username);
    }



