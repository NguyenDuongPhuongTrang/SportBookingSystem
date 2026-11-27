package com.example.it211project.service;

import com.example.it211project.model.dto.request.*;
import com.example.it211project.model.dto.response.UserResponse;
import com.example.it211project.model.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse register(RegisterRequest request);
    UserResponse createUser(CreateUserRequest request);
    Page<UserResponse> getUsers(String keyword, int page, int size);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    void changePassword(String username, ChangePasswordRequest request);
    User getUserEntityByUsername(String username);
    void forgotPassword(ForgotPasswordRequest request);
    UserResponse getProfile(String username);
    UserResponse updateProfile(String username, UpdateProfileRequest request);
}

