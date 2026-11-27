package com.example.it211project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtResponse {
    private Long id;
    private String courtName;
    private String location;
    private Double price;
    private List<String> imageUrls;
}
