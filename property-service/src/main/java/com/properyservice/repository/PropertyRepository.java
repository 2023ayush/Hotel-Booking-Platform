package com.properyservice.repository;

import com.properyservice.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

	@Query("""
SELECT DISTINCT p
FROM Property p
LEFT JOIN p.rooms r
LEFT JOIN RoomAvailability ra ON ra.room = r
WHERE (:city IS NULL OR LOWER(p.city.name) = LOWER(:city))
AND (:date IS NULL OR ra.availableDate = :date)
""")
	Page<Property> searchPropertyPaged(
			@Param("city") String city,
			@Param("date") LocalDate date,
			Pageable pageable
	);



}