package com.bookingservice.client;

import com.bookingservice.dto.APIResponse;
import com.bookingservice.dto.PropertyDto;
import com.bookingservice.dto.RoomAvailability;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "PROPERTY-SERVICE")
public interface PropertyClient {

    @GetMapping("/api/v1/property/property-id")
    public APIResponse<PropertyDto> getPropertyById(@RequestParam long id);

    @GetMapping("/api/v1/property/room-available-room-id")
    public APIResponse<List<RoomAvailability>> getTotalRoomsAvailable(@RequestParam long id);


    @GetMapping("/api/v1/property/room-id")
    public APIResponse<com.bookingservice.dto.Rooms> getRoomType(@RequestParam long id);
}