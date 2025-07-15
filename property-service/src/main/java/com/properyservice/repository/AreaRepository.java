package com.properyservice.repository;


import com.properyservice.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Area findByName(String name);

}