package com.example.it211project.test_controller;

import com.example.it211project.model.dto.request.CourtRequest;
import com.example.it211project.model.dto.response.CourtResponse;
import com.example.it211project.service.CourtService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class CourtControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CourtService courtService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private CourtResponse sampleCourt() {
        return CourtResponse.builder()
                .id(1L)
                .courtName("Sân A")
                .location("Vinh")
                .price(100.0)
                .imageUrls(List.of())
                .build();
    }

    @Test
    void getAllCourts_Success() throws Exception {
        when(courtService.getAllCourts(anyString())).thenReturn(List.of(sampleCourt()));

        mockMvc.perform(get("/api/v1/courts")
                        .param("keyword", "Vinh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].courtName").value("Sân A"));
    }

    @Test
    void getCourtById_Success() throws Exception {
        when(courtService.getCourtById(1L)).thenReturn(sampleCourt());

        mockMvc.perform(get("/api/v1/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void createCourt_Success() throws Exception {
        CourtRequest request = new CourtRequest();
        request.setCourtName("Sân Mới");
        request.setLocation("Hà Nội");
        request.setPrice(120.0);

        when(courtService.createCourt(any(CourtRequest.class))).thenReturn(sampleCourt());

        mockMvc.perform(post("/api/v1/courts")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.courtName").value("Sân A"));
    }

    @Test
    void updateCourt_Success() throws Exception {
        CourtRequest request = new CourtRequest();
        request.setCourtName("Sân Mới");
        request.setLocation("Hà Nội");
        request.setPrice(120.0);

        when(courtService.updateCourt(eq(1L), any(CourtRequest.class))).thenReturn(sampleCourt());

        mockMvc.perform(put("/api/v1/courts/1")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.courtName").value("Sân A"));
    }

    @Test
    void deleteCourt_Success() throws Exception {
        doNothing().when(courtService).deleteCourt(1L);

        mockMvc.perform(delete("/api/v1/courts/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Xóa sân thành công"));
    }
}
