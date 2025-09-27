package com.ceos22.cgv_clone.domain.theater.controller;

import com.ceos22.cgv_clone.domain.theater.dto.CinemaDetailDto;
import com.ceos22.cgv_clone.domain.theater.dto.CinemaSummaryDto;
import com.ceos22.cgv_clone.domain.theater.service.CinemaService;
import com.ceos22.cgv_clone.global.apiPayload.CustomResponse;
import com.ceos22.cgv_clone.global.apiPayload.code.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CinemaController {

    private final CinemaService cinemaService;

    /** 전체 영화관 목록 조회 */
    @GetMapping
    public CustomResponse<List<CinemaSummaryDto>> getCinemaList() {
        return CustomResponse.onSuccess(SuccessCode.OK, cinemaService.getCinemaList());
    }

    /** 특정 영화관 상세 정보 조회 */
    @GetMapping("/{cinemaId}")
    public CustomResponse<CinemaDetailDto> getCinemaDetail(@PathVariable Long cinemaId) {
        return CustomResponse.onSuccess(SuccessCode.OK, cinemaService.getCinema(cinemaId));
    }
}
