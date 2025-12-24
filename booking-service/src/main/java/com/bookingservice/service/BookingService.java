package com.bookingservice.service;

import com.bookingservice.client.PropertyClient;
import com.bookingservice.dto.*;
import com.bookingservice.entity.BookingDate;
import com.bookingservice.entity.Bookings;
import com.bookingservice.repository.BookingDateRepository;
import com.bookingservice.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final PropertyClient propertyClient;
    private final BookingRepository bookingRepository;
    private final BookingDateRepository bookingDateRepository;

    public BookingService(PropertyClient propertyClient,
                          BookingRepository bookingRepository,
                          BookingDateRepository bookingDateRepository) {
        this.propertyClient = propertyClient;
        this.bookingRepository = bookingRepository;
        this.bookingDateRepository = bookingDateRepository;
    }

    @CircuitBreaker(name = "propertyService", fallbackMethod = "propertyFallback")
    @Retry(name = "propertyService")
    public APIResponse<List<String>> addToCart(BookingDto bookingDto) {

        List<String> messages = new ArrayList<>();
        APIResponse<List<String>> apiResponse = new APIResponse<>();

        // Calls to property-service (PROTECTED)
        APIResponse<PropertyDto> response =
                propertyClient.getPropertyById(bookingDto.getPropertyId());

        APIResponse<Rooms> roomType =
                propertyClient.getRoomType(bookingDto.getRoomId());

        APIResponse<List<RoomAvailability>> totalRoomsAvailable =
                propertyClient.getTotalRoomsAvailable(bookingDto.getRoomId());

        List<RoomAvailability> availableRooms = totalRoomsAvailable.getData();

        for (LocalDate date : bookingDto.getDate()) {
            boolean isAvailable = availableRooms.stream()
                    .anyMatch(ra -> ra.getAvailableDate().equals(date)
                            && ra.getAvailableCount() > 0);

            if (!isAvailable) {
                messages.add("Room not available on: " + date);
                apiResponse.setMessage("Sold Out");
                apiResponse.setStatus(500);
                apiResponse.setData(messages);
                return apiResponse;
            }
        }

        Bookings bookings = new Bookings();
        bookings.setName(bookingDto.getName());
        bookings.setEmail(bookingDto.getEmail());
        bookings.setMobile(bookingDto.getMobile());
        bookings.setPropertyName(response.getData().getName());
        bookings.setStatus("pending");
        bookings.setTotalPrice(
                roomType.getData().getBasePrice() * bookingDto.getTotalNigths()
        );

        Bookings savedBooking = bookingRepository.save(bookings);

        for (LocalDate date : bookingDto.getDate()) {
            BookingDate bookingDate = new BookingDate();
            bookingDate.setDate(date);
            bookingDate.setBookings(savedBooking);
            bookingDateRepository.save(bookingDate);
        }

        messages.add("Booking saved with id: " + savedBooking.getId());
        apiResponse.setMessage("Booking added successfully");
        apiResponse.setStatus(201);
        apiResponse.setData(messages);
        return apiResponse;
    }

    // 🔁 FALLBACK METHOD (MANDATORY)
    public APIResponse<List<String>> propertyFallback(
            BookingDto bookingDto, Exception ex) {

        APIResponse<List<String>> apiResponse = new APIResponse<>();
        List<String> messages = new ArrayList<>();

        messages.add("Property service is currently unavailable");
        apiResponse.setMessage("Service temporarily unavailable");
        apiResponse.setStatus(503);
        apiResponse.setData(messages);

        return apiResponse;
    }
}
