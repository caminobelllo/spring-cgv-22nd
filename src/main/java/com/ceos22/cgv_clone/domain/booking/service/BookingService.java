package com.ceos22.cgv_clone.domain.booking.service;

import com.ceos22.cgv_clone.domain.booking.dto.request.BookingCancelRequestDto;
import com.ceos22.cgv_clone.domain.booking.dto.request.BookingRequestDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingCancelResponseDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingDetailResponseDto;
import com.ceos22.cgv_clone.domain.booking.dto.response.BookingResponseDto;
import com.ceos22.cgv_clone.domain.booking.eneity.Booking;
import com.ceos22.cgv_clone.domain.booking.eneity.BookingSeat;
import com.ceos22.cgv_clone.domain.booking.repository.BookingRepository;
import com.ceos22.cgv_clone.domain.booking.repository.BookingSeatRepository;
import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.domain.screening.entity.Screening;
import com.ceos22.cgv_clone.domain.screening.repository.ScreeningRepository;
import com.ceos22.cgv_clone.domain.theater.entity.Seat;
import com.ceos22.cgv_clone.domain.theater.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private static final int ADULT_PRICE = 15000;
    private static final int TEEN_PRICE  = 11000;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final MemberRepository memberRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;

    /** 예매 생성 */
    @Transactional
    public BookingResponseDto create(BookingRequestDto req) {
        // 1) 기본 로딩
        Member member = memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Screening screening = screeningRepository.findById(req.getScreeningId())
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        // 2) 입력 검증
        if (req.getSeatIds() == null || req.getSeatIds().isEmpty())
            throw new IllegalArgumentException("seatIds required");

        int expectedPeople = req.getAdultCount() + req.getTeenCount();
        if (expectedPeople != req.getSeatIds().size())
            throw new IllegalArgumentException("people count must match seat count");

        if (req.getPaymentType() == null)
            throw new IllegalArgumentException("paymentType required");

        // 3) 좌석 로딩 + 회차-관 일치 검증
        List<Seat> seats = seatRepository.findAllById(req.getSeatIds());
        if (seats.size() != req.getSeatIds().size())
            throw new IllegalArgumentException("some seats not found");

        Long screeningAuditoriumId = screening.getAuditorium().getId();
        boolean mismatch = seats.stream().anyMatch(s -> !Objects.equals(
                s.getAuditorium().getId(), screeningAuditoriumId));
        if (mismatch) throw new IllegalArgumentException("seat not in screening auditorium");

        // 4) 가격 계산
        int totalPrice = req.getAdultCount() * ADULT_PRICE + req.getTeenCount() * TEEN_PRICE;

        // 5) Booking 저장
        Booking booking = Booking.create(member, screening, req.getPaymentType(),
                req.getAdultCount(), req.getTeenCount(), totalPrice);
        bookingRepository.save(booking);

        // 6) BookingSeat 저장 (UNIQUE(screening, seat) 로 더블북킹 방지)
        List<BookingSeat> lines = new ArrayList<>();
        for (Seat seat : seats) {
            lines.add(BookingSeat.of(booking, screening, seat));
        }
        try {
            bookingSeatRepository.saveAll(lines);
        } catch (DataIntegrityViolationException e) {
            // 동시성 충돌(이미 누가 먼저 같은 좌석 예매)
            throw new IllegalStateException("Some seats already booked", e);
        }

        // 7) 응답
        List<String> seatLabels = seats.stream()
                .map(s -> s.getRowNo() + "-" + s.getColumnNo()) // 필요시 포맷 바꿔
                .toList();

        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .screeningId(screening.getId())
                .adultCount(booking.getAdultCount())
                .teenCount(booking.getTeenCount())
                .totalPeople(booking.getTotalPeople())
                .totalPrice(booking.getTotalPrice())
                .paymentType(booking.getPaymentType())
                .status(booking.getStatus())
                .bookingAt(booking.getBookingAt())
                .seats(seatLabels)
                .build();
    }

}
