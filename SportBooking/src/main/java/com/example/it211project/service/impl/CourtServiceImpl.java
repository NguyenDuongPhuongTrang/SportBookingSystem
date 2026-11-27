package com.example.it211project.service.impl;

import com.example.it211project.exception.ResourceNotFoundException;
import com.example.it211project.model.dto.request.CourtRequest;
import com.example.it211project.model.dto.response.CourtResponse;
import com.example.it211project.model.entity.Court;
import com.example.it211project.model.entity.CourtImage;
import com.example.it211project.repository.CourtRepository;
import com.example.it211project.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {
    private final CourtRepository courtRepository;

    @Override
    public List<CourtResponse> getAllCourts(String keyword) {
        List<Court> courts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            courts = courtRepository.findAll().stream()
                    .filter(c -> c.getCourtName().toLowerCase().contains(keyword.toLowerCase())
                            || c.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            courts = courtRepository.findAll();
        }
        return courts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CourtResponse getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với id: " + id));
        return mapToResponse(court);
    }

    @Override
    public CourtResponse createCourt(CourtRequest request) {
        Court court = Court.builder()
                .courtName(request.getCourtName())
                .location(request.getLocation())
                .price(request.getPrice())
                .build();
        courtRepository.save(court);
        return mapToResponse(court);
    }

    @Override
    public CourtResponse updateCourt(Long id, CourtRequest request) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với id: " + id));
        court.setCourtName(request.getCourtName());
        court.setLocation(request.getLocation());
        court.setPrice(request.getPrice());
        courtRepository.save(court);
        return mapToResponse(court);
    }

    @Override
    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với id: " + id));
        courtRepository.delete(court);
    }

    private CourtResponse mapToResponse(Court court) {
        List<String> imageUrls = court.getImages() != null ?
                court.getImages().stream().map(CourtImage::getImageUrl).collect(Collectors.toList()) :
                List.of();
        return CourtResponse.builder()
                .id(court.getId())
                .courtName(court.getCourtName())
                .location(court.getLocation())
                .price(court.getPrice())
                .imageUrls(imageUrls)
                .build();
    }
}
