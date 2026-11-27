package com.example.it211project.test_service;

import com.example.it211project.enums.BookingStatus;
import com.example.it211project.enums.Role;
import com.example.it211project.exception.ConflictException;
import com.example.it211project.exception.ResourceNotFoundException;
import com.example.it211project.model.dto.request.BookingRequest;
import com.example.it211project.model.dto.response.BookingResponse;
import com.example.it211project.model.entity.Booking;
import com.example.it211project.model.entity.Court;
import com.example.it211project.model.entity.User;
import com.example.it211project.repository.BookingRepository;
import com.example.it211project.repository.CourtRepository;
import com.example.it211project.repository.UserRepository;
import com.example.it211project.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_Success() {
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("08:00-09:00");

        User customer = User.builder().id(10L).username("customer1").role(Role.ROLE_CUSTOMER).build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").location("Hà Nội").price(150000.0).build();

        when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlot(1L, request.getBookingDate(), "08:00-09:00"))
                .thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        BookingResponse response = bookingService.createBooking(10L, request);

        assertNotNull(response);
        assertEquals("Sân Tennis A", response.getCourtName());
        assertEquals("customer1", response.getCustomerName());
        assertEquals(BookingStatus.PENDING, response.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_Conflict_ThrowsConflictException() {
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("08:00-09:00");

        when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlot(1L, request.getBookingDate(), "08:00-09:00"))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> bookingService.createBooking(10L, request));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_Success() {
        User customer = User.builder().id(10L).username("customer1").build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").build();
        Booking booking = Booking.builder()
                .id(100L)
                .customer(customer)
                .court(court)
                .bookingDate(LocalDate.now())
                .timeSlot("08:00-09:00")
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Sân Tennis A", response.getCourtName());
    }

    @Test
    void getBookingById_NotFound_ThrowsResourceNotFoundException() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(100L));
    }

    @Test
    void getMyBookings_Success() {
        User customer = User.builder().id(10L).username("customer1").build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").build();
        Booking booking1 = Booking.builder()
                .id(100L)
                .customer(customer)
                .court(court)
                .bookingDate(LocalDate.now())
                .timeSlot("08:00-09:00")
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findByCustomerId(10L)).thenReturn(List.of(booking1));

        List<BookingResponse> responses = bookingService.getMyBookings(10L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("customer1", responses.get(0).getCustomerName());
    }

    @Test
    void updateStatus_Success() {
        User customer = User.builder().id(10L).username("customer1").build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").build();
        Booking booking = Booking.builder()
                .id(100L)
                .customer(customer)
                .court(court)
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.updateStatus(100L, BookingStatus.CONFIRMED);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void getAllBookings_Success() {
        User customer = User.builder().id(10L).username("customer1").build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").build();
        Booking booking = Booking.builder()
                .id(100L)
                .customer(customer)
                .court(court)
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingResponse> list = bookingService.getAllBookings();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void getBookedSlots_FilterRejected_Success() {
        User customer = User.builder().id(10L).username("customer1").build();
        Court court = Court.builder().id(1L).courtName("Sân Tennis A").build();
        Booking booking1 = Booking.builder()
                .id(100L)
                .customer(customer)
                .court(court)
                .timeSlot("08:00-09:00")
                .status(BookingStatus.PENDING)
                .build();
        Booking booking2 = Booking.builder()
                .id(101L)
                .customer(customer)
                .court(court)
                .timeSlot("09:00-10:00")
                .status(BookingStatus.REJECTED)
                .build();

        LocalDate date = LocalDate.now();
        when(bookingRepository.findByCourtIdAndBookingDate(1L, date)).thenReturn(List.of(booking1, booking2));

        List<String> bookedSlots = bookingService.getBookedSlots(1L, date);

        assertNotNull(bookedSlots);
        assertEquals(1, bookedSlots.size());
        assertEquals("08:00-09:00", bookedSlots.get(0));
    }
}
