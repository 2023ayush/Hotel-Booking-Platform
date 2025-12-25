package com.bookingservice.controller;

import com.bookingservice.dto.*;
import com.bookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<APIResponse<BookingResponseDto>> addToCart(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String username,
            @RequestBody BookingDto bookingDto) {

        BookingResponseDto dto = bookingService.addToCart(bookingDto, username, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Booking added successfully", dto));
    }

    @PostMapping("/checkout")
    public ResponseEntity<APIResponse<BookingResponseDto>> checkout(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String username,
            @RequestBody CheckoutDto checkoutDto) {

        BookingResponseDto dto = bookingService.checkout(checkoutDto, username, role);
        return ResponseEntity.ok(new APIResponse<>(200, "Checkout successful", dto));
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse<List<BookingResponseDto>>> listBookings(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String username) {

        List<BookingResponseDto> list = bookingService.listBookings(username, role);
        return ResponseEntity.ok(new APIResponse<>(200, "Booking list retrieved", list));
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<APIResponse<String>> cancelBooking(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String username,
            @PathVariable Long bookingId) {

        String message = bookingService.cancelBooking(bookingId, username, role);
        return ResponseEntity.ok(new APIResponse<>(200, message, message));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<APIResponse<List<BookingResponseDto>>> listAllBookings(
            @RequestHeader("X-User-Role") String role) {

        List<BookingResponseDto> list = bookingService.listAllBookings(role);
        return ResponseEntity.ok(new APIResponse<>(200, "All bookings retrieved", list));
    }
}
