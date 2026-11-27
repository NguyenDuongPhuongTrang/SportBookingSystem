package com.example.it211project.test_service;

import com.example.it211project.exception.ResourceNotFoundException;
import com.example.it211project.model.dto.request.CourtRequest;
import com.example.it211project.model.dto.response.CourtResponse;
import com.example.it211project.model.entity.Court;
import com.example.it211project.model.entity.CourtImage;
import com.example.it211project.repository.CourtRepository;
import com.example.it211project.service.impl.CourtServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtServiceImpl courtService;

    @Test
    void getAllCourts_NoKeyword_Success() {
        Court court1 = Court.builder().id(1L).courtName("Sân A").location("Vinh").price(100.0).build();
        Court court2 = Court.builder().id(2L).courtName("Sân B").location("Hà Nội").price(200.0).build();

        when(courtRepository.findAll()).thenReturn(List.of(court1, court2));

        List<CourtResponse> responses = courtService.getAllCourts(null);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Sân A", responses.get(0).getCourtName());
    }

    @Test
    void getAllCourts_WithKeyword_Success() {
        Court court1 = Court.builder().id(1L).courtName("Sân A").location("Vinh").price(100.0).build();
        Court court2 = Court.builder().id(2L).courtName("Sân B").location("Hà Nội").price(200.0).build();

        when(courtRepository.findAll()).thenReturn(List.of(court1, court2));

        List<CourtResponse> responses = courtService.getAllCourts("Vinh");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Sân A", responses.get(0).getCourtName());
    }

    @Test
    void getCourtById_Success() {
        Court court = Court.builder().id(1L).courtName("Sân A").location("Vinh").price(100.0).build();
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        CourtResponse response = courtService.getCourtById(1L);

        assertNotNull(response);
        assertEquals("Sân A", response.getCourtName());
    }

    @Test
    void getCourtById_NotFound_ThrowsResourceNotFoundException() {
        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courtService.getCourtById(99L));
    }

    @Test
    void createCourt_Success() {
        CourtRequest request = new CourtRequest();
        request.setCourtName("Sân Tennis Mới");
        request.setLocation("Hải Phòng");
        request.setPrice(120.0);

        CourtResponse response = courtService.createCourt(request);

        assertNotNull(response);
        assertEquals("Sân Tennis Mới", response.getCourtName());
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    void updateCourt_Success() {
        Court court = Court.builder().id(1L).courtName("Sân Cũ").location("Vinh").price(100.0).build();
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        CourtRequest request = new CourtRequest();
        request.setCourtName("Sân Mới");
        request.setLocation("Đà Nẵng");
        request.setPrice(150.0);

        CourtResponse response = courtService.updateCourt(1L, request);

        assertNotNull(response);
        assertEquals("Sân Mới", response.getCourtName());
        assertEquals("Đà Nẵng", response.getLocation());
        assertEquals(150.0, response.getPrice());
        verify(courtRepository, times(1)).save(court);
    }

    @Test
    void updateCourt_NotFound_ThrowsResourceNotFoundException() {
        when(courtRepository.findById(1L)).thenReturn(Optional.empty());
        CourtRequest request = new CourtRequest();

        assertThrows(ResourceNotFoundException.class, () -> courtService.updateCourt(1L, request));
    }

    @Test
    void deleteCourt_Success() {
        Court court = Court.builder().id(1L).build();
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        courtService.deleteCourt(1L);

        verify(courtRepository, times(1)).delete(court);
    }

    @Test
    void deleteCourt_NotFound_ThrowsResourceNotFoundException() {
        when(courtRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courtService.deleteCourt(1L));
    }
}
