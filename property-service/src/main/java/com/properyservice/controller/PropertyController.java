package com.properyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.properyservice.dto.*;
import com.properyservice.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    public PropertyController(PropertyService propertyService, ObjectMapper objectMapper) {
        this.propertyService = propertyService;
        this.objectMapper = objectMapper;
    }

    // ---------------- ADD PROPERTY ----------------
    @PostMapping
// Remove consumes Multipart
    public ResponseEntity<APIResponse<PropertyDto>> addProperty(
            @RequestBody PropertyDto dto
    ) {
        PropertyDto saved = propertyService.addProperty(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Property added successfully", saved));
    }


    // ---------------- SEARCH PROPERTY WITH PAGINATION ----------------
    @GetMapping("/search-paged")
    public ResponseEntity<APIResponse<Page<PropertyDto>>> searchPropertyPaged(
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                propertyService.searchProperty(city, date, page, size, sortBy)
        );
    }



    // ---------------- GET PROPERTY BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<PropertyDto>> getPropertyById(@PathVariable long id) {
        return ResponseEntity.ok(propertyService.findPropertyById(id));
    }

    // ---------------- UPDATE PROPERTY ----------------
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<PropertyDto>> updateProperty(
            @PathVariable long id,
            @RequestBody PropertyDto dto
    ) {
        PropertyDto updated = propertyService.updateProperty(id, dto);
        return ResponseEntity.ok(new APIResponse<>(200, "Property updated", updated));
    }

    // ---------------- DELETE PROPERTY ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteProperty(@PathVariable long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Property deleted", null));
    }

    // ---------------- ADD ROOM ----------------
    @PostMapping("/{propertyId}/rooms")
    public ResponseEntity<APIResponse<RoomsDto>> addRoom(
            @PathVariable long propertyId,
            @RequestBody RoomsDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Room added", propertyService.addRoom(propertyId, dto)));
    }

    // ---------------- UPDATE ROOM ----------------
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<APIResponse<RoomsDto>> updateRoom(
            @PathVariable long roomId,
            @RequestBody RoomsDto dto
    ) {
        return ResponseEntity.ok(new APIResponse<>(200, "Room updated", propertyService.updateRoom(roomId, dto)));
    }

    // ---------------- GET ROOM ----------------
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<APIResponse<RoomsDto>> getRoom(@PathVariable long roomId) {
        return ResponseEntity.ok(new APIResponse<>(200, "Room details", propertyService.getRoomById(roomId)));
    }

    // ---------------- ROOM AVAILABILITY ----------------
    @GetMapping("/rooms/{roomId}/availability")
    public ResponseEntity<APIResponse<List<RoomAvailabilityDto>>> roomAvailability(@PathVariable long roomId) {
        return ResponseEntity.ok(new APIResponse<>(200, "Room availability",
                propertyService.getTotalRoomsAvailable(roomId)));
    }

    // ---------------- ADD / UPDATE AVAILABILITY ----------------
    @PostMapping("/rooms/{roomId}/availability")
    public ResponseEntity<APIResponse<Void>> addAvailability(
            @PathVariable long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int count
    ) {
        propertyService.addOrUpdateAvailability(roomId, date, count);
        return ResponseEntity.ok(new APIResponse<>(200, "Availability updated", null));
    }
}
