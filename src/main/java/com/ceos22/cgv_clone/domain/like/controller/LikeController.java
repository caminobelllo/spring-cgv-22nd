package com.ceos22.cgv_clone.domain.like.controller;

import com.ceos22.cgv_clone.domain.like.dto.LikeToggleResponseDto;
import com.ceos22.cgv_clone.domain.like.service.LikeService;
import com.ceos22.cgv_clone.global.apiPayload.CustomResponse;
import com.ceos22.cgv_clone.global.apiPayload.code.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likes/movies/{movieId}/toggle")
    public CustomResponse<LikeToggleResponseDto> toggleMovieLike(
            @PathVariable Long movieId,
            @RequestParam Long memberId ) { // TODO: @AuthenticationPrincipal

        LikeToggleResponseDto response = likeService.toggleMovieLike(memberId, movieId);
        return CustomResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @PostMapping("/cinemas/{cinemaId}/toggle")
    public CustomResponse<LikeToggleResponseDto> toggleCinemaLike(
            @PathVariable Long cinemaId,
            @RequestParam Long memberId) { // TODO: @AuthenticationPrincipal

        LikeToggleResponseDto response = likeService.toggleCinemaLike(memberId, cinemaId);
        return CustomResponse.onSuccess(SuccessCode.CREATED, response);
    }
}
