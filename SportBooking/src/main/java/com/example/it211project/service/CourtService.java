package com.example.it211project.service;

import com.example.it211project.model.dto.request.CourtRequest;
import com.example.it211project.model.dto.response.CourtResponse;
import java.util.List;

public interface CourtService {
    List<CourtResponse> getAllCourts(String keyword);
    CourtResponse getCourtById(Long id);
    CourtResponse createCourt(CourtRequest request);
    CourtResponse updateCourt(Long id, CourtRequest request);
    void deleteCourt(Long id);
}
