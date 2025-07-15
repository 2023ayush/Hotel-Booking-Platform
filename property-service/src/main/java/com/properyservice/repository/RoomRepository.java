package com.properyservice.repository;

import com.properyservice.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Rooms, Long> {



}
