package com.properyservice.repository;


import com.properyservice.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
    State findByName(String name);
}