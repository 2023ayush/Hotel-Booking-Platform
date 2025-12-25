package com.properyservice.repository;

import com.properyservice.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository
        extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByRoomId(long roomId);

    Optional<RoomAvailability> findByRoomIdAndAvailableDate(
            long roomId,
            LocalDate availableDate
    );
}
