package com.properyservice.repository;

import com.properyservice.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    public List<RoomAvailability> findByRoomId(long id);
}