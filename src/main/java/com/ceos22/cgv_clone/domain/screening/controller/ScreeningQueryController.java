package com.ceos22.cgv_clone.domain.screening.controller;


import com.ceos22.cgv_clone.domain.screening.dto.ScreeningDto;
import com.ceos22.cgv_clone.domain.screening.service.ScreeningQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScreeningQueryController {

    private final ScreeningQueryService service;

    // 예: /api/screenings?cinemaId=1&date=2025-10-10
    //     /api/screenings?cinemaId=1&movieId=7&date=2025-10-10
    //     /api/screenings?auditoriumId=3&date=2025-10-10
    @GetMapping("/screenings")
    public List<ScreeningDto> getScreeningList(
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Long auditoriumId,
            @RequestParam(required = false) Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        return service.findByConditions(cinemaId, auditoriumId, movieId, date);
    }
}
