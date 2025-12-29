package com.bookingservice.service;

import com.bookingservice.client.PropertyClient;
import com.bookingservice.dto.*;
import com.bookingservice.entity.BookingDate;
import com.bookingservice.entity.BookingStatus;
import com.bookingservice.entity.Bookings;
import com.bookingservice.exception.InvalidRequestException;
import com.bookingservice.exception.ResourceNotFoundException;
import com.bookingservice.repository.BookingDateRepository;
import com.bookingservice.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public BookingResponseDto addToCart(BookingDto bookingDto, String username, String role) {

        // Role check
        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
            throw new InvalidRequestException("Forbidden");
        }

        // Input validation
        if (bookingDto.getPropertyId() <= 0 || bookingDto.getRoomId() <= 0
                || bookingDto.getTotalNigths() <= 0 || bookingDto.getDate() == null || bookingDto.getDate().isEmpty()) {
            throw new InvalidRequestException("Invalid input");
        }

        // Fetch property and room info from property-service
        PropertyDto property = propertyClient.getPropertyById(bookingDto.getPropertyId())
                .getData();
        Rooms room = propertyClient.getRoomType(bookingDto.getRoomId()).getData();
        List<RoomAvailability> availableRooms = propertyClient.getTotalRoomsAvailable(bookingDto.getRoomId()).getData();

        // Check room availability
        for (LocalDate date : bookingDto.getDate()) {
            boolean isAvailable = availableRooms.stream()
                    .anyMatch(ra -> ra.getAvailableDate().equals(date) && ra.getAvailableCount() > 0);
            if (!isAvailable) {
                throw new InvalidRequestException("Room not available on: " + date);
            }
        }

        // Save booking
        Bookings booking = new Bookings();
        booking.setName(bookingDto.getName());
        booking.setEmail(bookingDto.getEmail());
        booking.setMobile(bookingDto.getMobile());
        booking.setPropertyName(property.getName());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(room.getBasePrice() * bookingDto.getTotalNigths());
        booking.setUsername(username);

        Bookings savedBooking = bookingRepository.save(booking);

        for (LocalDate date : bookingDto.getDate()) {
            BookingDate bookingDate = new BookingDate();
            bookingDate.setBookings(savedBooking);
            bookingDate.setDate(date);
            bookingDateRepository.save(bookingDate);
        }

        // Build response DTO
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setBookingId(savedBooking.getId());
        responseDto.setPropertyName(savedBooking.getPropertyName());
        responseDto.setStatus(savedBooking.getStatus());
        responseDto.setTotalPrice(savedBooking.getTotalPrice());
        responseDto.setDates(bookingDto.getDate());

        return responseDto;
    }

    public BookingResponseDto checkout(CheckoutDto dto, String username, String role) {
        if (!role.equals("ROLE_USER")) {
            throw new InvalidRequestException("Forbidden");
        }

        Bookings booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUsername().equals(username)) {
            throw new InvalidRequestException("You cannot checkout this booking");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setBookingId(booking.getId());
        responseDto.setPropertyName(booking.getPropertyName());
        responseDto.setStatus(booking.getStatus());
        responseDto.setTotalPrice(booking.getTotalPrice());
        return responseDto;
    }

    public List<BookingResponseDto> listBookings(String username, String role) {
        List<Bookings> bookings;
        if (role.equals("ROLE_ADMIN")) {
            bookings = bookingRepository.findAll();
        } else {
            bookings = bookingRepository.findByUsername(username);
        }

        return bookings.stream().map(b -> {
            BookingResponseDto dto = new BookingResponseDto();
            dto.setBookingId(b.getId());
            dto.setPropertyName(b.getPropertyName());
            dto.setStatus(b.getStatus());
            dto.setTotalPrice(b.getTotalPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    public String cancelBooking(Long bookingId, String username, String role) {
        if (!role.equals("ROLE_USER")) {
            throw new InvalidRequestException("Forbidden");
        }

        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUsername().equals(username)) {
            throw new InvalidRequestException("You cannot cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return "Booking ID " + bookingId + " cancelled";
    }

    public List<BookingResponseDto> listAllBookings(String role) {
        if (!role.equals("ROLE_ADMIN")) {
            throw new InvalidRequestException("Forbidden");
        }

        List<Bookings> bookings = bookingRepository.findAll();
        return bookings.stream().map(b -> {
            BookingResponseDto dto = new BookingResponseDto();
            dto.setBookingId(b.getId());
            dto.setPropertyName(b.getPropertyName());
            dto.setStatus(b.getStatus());
            dto.setTotalPrice(b.getTotalPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    // Fallback method for circuit breaker
    public BookingResponseDto propertyFallback(BookingDto bookingDto, String username, String role, Exception ex) {
        throw new InvalidRequestException("Property service is temporarily unavailable");
    }
}
