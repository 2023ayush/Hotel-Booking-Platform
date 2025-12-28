package com.bookingservice.Service;

import com.bookingservice.client.PropertyClient;
import com.bookingservice.dto.*;
import com.bookingservice.entity.*;
import com.bookingservice.exception.InvalidRequestException;
import com.bookingservice.repository.BookingDateRepository;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.service.BookingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingDateRepository bookingDateRepository;

    @Mock
    private PropertyClient propertyClient;

    @InjectMocks
    private BookingService bookingService;

    // =========================
    // ADD TO CART - SUCCESS
    // =========================
    @Test
    void testAddToCart_Success() {

        // -------- Booking DTO --------
        BookingDto bookingDto = new BookingDto();
        bookingDto.setPropertyId(1L);
        bookingDto.setRoomId(1L);
        bookingDto.setTotalNigths(1);
        bookingDto.setDate(List.of(LocalDate.of(2025, 12, 27)));
        bookingDto.setName("John");
        bookingDto.setEmail("john@example.com");
        bookingDto.setMobile("1234567890");

        // -------- Property DTO --------
        PropertyDto propertyDto = new PropertyDto();
        propertyDto.setId(1L);
        propertyDto.setName("Luxury Villa");

        // -------- Room DTO --------
        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomType("Deluxe");
        room.setBasePrice(200.0);


        // -------- Availability --------
        RoomAvailability availability = new RoomAvailability();
        availability.setRoomId(1L);
        availability.setDate(LocalDate.of(2025, 12, 27));
        availability.setAvailableRooms(5);

        // -------- Mocks --------
        when(propertyClient.getPropertyById(1L))
                .thenReturn(new APIResponse<>(200, "ok", propertyDto));

        when(propertyClient.getRoomType(1L))
                .thenReturn(new APIResponse<>(200, "ok", room));

        when(propertyClient.getTotalRoomsAvailable(1L))
                .thenReturn(new APIResponse<>(200, "ok", List.of(availability)));

        when(bookingDateRepository.existsByRoomIdAndDateAndUsername(anyLong(), any(), any()))
                .thenReturn(false);

        when(bookingRepository.save(any(Bookings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // -------- Execute --------
        BookingResponseDto response =
                bookingService.addToCart(bookingDto, "john1", "ROLE_USER");

        // -------- Verify --------
        assertEquals("Luxury Villa", response.getPropertyName());
        assertEquals(200.0, response.getTotalPrice());
        assertEquals(BookingStatus.PENDING, response.getStatus());
    }

    // =========================
    // ADD TO CART - DUPLICATE
    // =========================
    @Test
    void testAddToCart_DuplicateBooking() {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setPropertyId(1L);
        bookingDto.setRoomId(1L);
        bookingDto.setTotalNigths(1);
        bookingDto.setDate(List.of(LocalDate.of(2025, 12, 27)));

        // ---- Property mock (REQUIRED) ----
        PropertyDto propertyDto = new PropertyDto();
        propertyDto.setId(1L);
        propertyDto.setName("Luxury Villa");

        // ---- Room mock (FIXED) ----
        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomType("Deluxe");   // NOT setName()
        room.setBasePrice(200.0);


        RoomAvailability availability = new RoomAvailability();
        availability.setRoomId(1L);
        availability.setDate(LocalDate.of(2025, 12, 27));
        availability.setAvailableRooms(5);

        when(propertyClient.getPropertyById(1L))
                .thenReturn(new APIResponse<>(200, "ok", propertyDto));

        when(propertyClient.getRoomType(1L))
                .thenReturn(new APIResponse<>(200, "ok", room));


        when(propertyClient.getTotalRoomsAvailable(1L))
                .thenReturn(new APIResponse<>(200, "ok", List.of(availability)));

        when(bookingDateRepository.existsByRoomIdAndDateAndUsername(
                1L, LocalDate.of(2025, 12, 27), "john1"))
                .thenReturn(true);

        // ---- Assert ----
        InvalidRequestException ex = assertThrows(
                InvalidRequestException.class,
                () -> bookingService.addToCart(bookingDto, "john1", "ROLE_USER")
        );

        assertTrue(ex.getMessage().contains("already"));
    }


    // =========================
    // CHECKOUT - SUCCESS
    // =========================
    @Test
    void testCheckout_Success() {

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setUsername("john1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setPropertyName("Luxury Villa");
        booking.setTotalPrice(200.0);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        CheckoutDto checkoutDto = new CheckoutDto();
        checkoutDto.setBookingId(1L);

        BookingResponseDto response =
                bookingService.checkout(checkoutDto, "john1", "ROLE_USER");

        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals("Luxury Villa", response.getPropertyName());
    }
}
