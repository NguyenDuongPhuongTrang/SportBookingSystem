package com.example.it211project.test_controller;

import com.example.it211project.enums.Role;
import com.example.it211project.model.dto.request.ChangePasswordRequest;
import com.example.it211project.model.dto.request.UpdateProfileRequest;
import com.example.it211project.model.dto.response.UserResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private UserResponse sampleUser() {
        return UserResponse.builder()
                .id(10L)
                .username("customer1")
                .email("customer1@gmail.com")
                .role(Role.ROLE_CUSTOMER)
                .active(true)
                .build();
    }

    @Test
    void getProfile_Success() throws Exception {
        when(userService.getProfile("customer1")).thenReturn(sampleUser());

        mockMvc.perform(get("/api/v1/users/me")
                        .with(user("customer1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.username").value("customer1"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("updateduser");
        request.setEmail("updateduser@gmail.com");

        UserResponse updatedResponse = sampleUser();
        updatedResponse.setUsername("updateduser");
        updatedResponse.setEmail("updateduser@gmail.com");

        when(userService.updateProfile(eq("customer1"), any(UpdateProfileRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/me")
                        .with(user("customer1").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.username").value("updateduser"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("654321");

        doNothing().when(userService).changePassword(eq("customer1"), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/api/v1/users/change-password")
                        .with(user("customer1").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Đổi mật khẩu thành công"));
    }
}
