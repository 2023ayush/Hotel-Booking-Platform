package com.bookingservice.client;

import com.bookingservice.dto.APIResponse;
import com.bookingservice.dto.PropertyDto;
import com.bookingservice.dto.RoomAvailability;
import com.bookingservice.dto.Rooms;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "PROPERTYSERVICE")
public interface PropertyClient {

    // GET /api/v1/properties/{id}
    @GetMapping("/api/v1/properties/{id}")
    APIResponse<PropertyDto> getPropertyById(
            @PathVariable("id") long id
    );

    // GET /api/v1/properties/rooms/{roomId}/availability
    @GetMapping("/api/v1/properties/rooms/{roomId}/availability")
    APIResponse<List<RoomAvailability>> getTotalRoomsAvailable(
            @PathVariable("roomId") long roomId
    );

    // GET /api/v1/properties/rooms/{roomId}
    @GetMapping("/api/v1/properties/rooms/{roomId}")
    APIResponse<Rooms> getRoomType(
            @PathVariable("roomId") long roomId
    );
}
