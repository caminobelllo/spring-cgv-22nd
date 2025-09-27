package com.ceos22.cgv_clone.domain.theater.controller;
import com.ceos22.cgv_clone.domain.theater.dto.SeatStatusDto;
import com.ceos22.cgv_clone.domain.theater.service.SeatQueryService;
import com.ceos22.cgv_clone.global.apiPayload.CustomResponse;
import com.ceos22.cgv_clone.global.apiPayload.code.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeatQueryController {

    private final SeatQueryService service;

    // 좌석 현황
    @GetMapping("/screenings/{screeningId}/seats")
    public CustomResponse<List<SeatStatusDto>> seatMap(@PathVariable Long screeningId) {
        return CustomResponse.onSuccess(SuccessCode.OK, service.getSeatMap(screeningId));
    }
}
