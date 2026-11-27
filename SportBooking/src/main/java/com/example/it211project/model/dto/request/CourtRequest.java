package com.example.it211project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CourtRequest {
    @NotBlank(message = "Tên sân không được để trống")
    private String courtName;

    @NotBlank(message = "Vị trí không được để trống")
    private String location;

    @NotNull(message = "Giá thuê không được để trống")
    @Positive(message = "Giá thuê phải lớn hơn 0")
    private Double price;
}
