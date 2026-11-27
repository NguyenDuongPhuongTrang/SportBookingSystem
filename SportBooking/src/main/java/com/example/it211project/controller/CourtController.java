package com.example.it211project.controller;

import com.example.it211project.model.dto.request.CourtRequest;
import com.example.it211project.model.dto.response.ApiResponse;
import com.example.it211project.model.dto.response.CourtResponse;
import com.example.it211project.service.CourtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {
    private final CourtService courtService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourtResponse>>> getAllCourts(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sân thành công",
                courtService.getAllCourts(keyword)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtResponse>> getCourtById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy thông tin sân thành công",
                courtService.getCourtById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourtResponse>> createCourt(@Valid @RequestBody CourtRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Tạo sân thành công",
                courtService.createCourt(request)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtResponse>> updateCourt(@PathVariable Long id, @Valid @RequestBody CourtRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cập nhật sân thành công",
                courtService.updateCourt(id, request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Xóa sân thành công",
                null
        ));
    }
}
