package com.ceos22.cgv_clone.domain.booking.controller;

import com.ceos22.cgv_clone.domain.booking.dto.request.BookingRequestDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingCancelResponseDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingDetailResponseDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingResponseDto;
import com.ceos22.cgv_clone.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    /** 예매 생성 */
    @PostMapping("/booking")
    public ResponseEntity<BookingResponseDto> create(@RequestBody BookingRequestDto req) {
        return ResponseEntity.ok(bookingService.create(req));
    }

    /** 예매 상세 조회 */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BookingDetailResponseDto> getDetail(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getDetail(bookingId));
    }

    /** 예매 취소 (PathVar만 사용) */
    @PostMapping("/booking/{bookingId}/cancel")
    public ResponseEntity<BookingCancelResponseDto> cancel(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancel(bookingId));
    }
}
