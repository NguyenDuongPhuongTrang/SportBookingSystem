package com.example.it211project.test_controller;

import com.example.it211project.enums.BookingStatus;
import com.example.it211project.model.dto.request.BookingRequest;
import com.example.it211project.model.dto.request.UpdateBookingStatusRequest;
import com.example.it211project.model.dto.response.BookingResponse;
import com.example.it211project.model.entity.User;
import com.example.it211project.service.BookingService;
import com.example.it211project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BookingService bookingService;

    @MockitoBean
    UserService userService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private BookingResponse sampleBooking() {
        return BookingResponse.builder()
                .id(1L)
                .customerId(10L)
                .customerName("customer1")
                .courtId(2L)
                .courtName("Sân A")
                .bookingDate(LocalDate.now())
                .timeSlot("08:00-09:00")
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    void createBooking_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCourtId(2L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("08:00-09:00");

        User userEntity = User.builder().id(10L).username("customer1").build();
        when(userService.getUserEntityByUsername("customer1")).thenReturn(userEntity);
        when(bookingService.createBooking(eq(10L), any(BookingRequest.class))).thenReturn(sampleBooking());

        mockMvc.perform(post("/api/v1/bookings")
                        .with(user("customer1").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getAllBookings_Success() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/v1/bookings")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void getBookingById_Success() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(sampleBooking());

        mockMvc.perform(get("/api/v1/bookings/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void myBookings_Success() throws Exception {
        User userEntity = User.builder().id(10L).username("customer1").build();
        when(userService.getUserEntityByUsername("customer1")).thenReturn(userEntity);
        when(bookingService.getMyBookings(10L)).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/v1/bookings/my-bookings")
                        .with(user("customer1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void updateStatus_Success() throws Exception {
        UpdateBookingStatusRequest request = new UpdateBookingStatusRequest();
        request.setStatus(BookingStatus.CONFIRMED);

        BookingResponse updated = sampleBooking();
        updated.setStatus(BookingStatus.CONFIRMED);

        when(bookingService.updateStatus(1L, BookingStatus.CONFIRMED)).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/bookings/1/status")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    void getBookedSlots_Success() throws Exception {
        when(bookingService.getBookedSlots(eq(2L), any(LocalDate.class))).thenReturn(List.of("08:00-09:00"));

        mockMvc.perform(get("/api/v1/bookings/booked-slots")
                        .param("courtId", "2")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0]").value("08:00-09:00"));
    }
}
