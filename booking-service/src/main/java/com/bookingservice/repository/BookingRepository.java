package com.bookingservice.repository;

import com.bookingservice.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Bookings, Long> {
    // @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
    //        "FROM HotelBooking b " +
    //        "WHERE b.roomId = :roomId AND b.checkInDate < :newCheckOut AND b.checkOutDate > :newCheckIn")
    // boolean existsOverlappingBooking(@Param("roomId") Long roomId,
    //                                  @Param("newCheckIn") LocalDate newCheckIn,
    //                                  @Param("newCheckOut") LocalDate newCheckOut);
}
