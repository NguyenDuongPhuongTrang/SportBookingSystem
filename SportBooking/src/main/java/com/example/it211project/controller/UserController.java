package com.example.it211project.controller;

import com.example.it211project.model.dto.request.ChangePasswordRequest;
import com.example.it211project.model.dto.request.UpdateProfileRequest;
import com.example.it211project.model.dto.response.ApiResponse;
import com.example.it211project.model.dto.response.UserResponse;
import com.example.it211project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Lấy thông tin cá nhân thành công",
                userService.getProfile(authentication.getName())
        ), HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(Authentication authentication,
                                                                   @Valid @RequestBody UpdateProfileRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Cập nhật thông tin cá nhân thành công",
                userService.updateProfile(authentication.getName(), request)
        ), HttpStatus.OK);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(Authentication authentication,
                                                            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(authentication.getName(), request);
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Đổi mật khẩu thành công",
                null
        ), HttpStatus.OK);
    }
}
