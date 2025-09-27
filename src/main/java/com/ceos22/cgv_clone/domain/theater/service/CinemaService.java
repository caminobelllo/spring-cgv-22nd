package com.ceos22.cgv_clone.domain.theater.service;

import com.ceos22.cgv_clone.domain.theater.dto.AuditoriumDto;
import com.ceos22.cgv_clone.domain.theater.dto.CinemaDetailDto;
import com.ceos22.cgv_clone.domain.theater.dto.CinemaSummaryDto;
import com.ceos22.cgv_clone.domain.theater.entity.Cinema;
import com.ceos22.cgv_clone.domain.theater.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    /** 전체 영화관 목록 조회 */
    public List<CinemaSummaryDto> getCinemaList() {

        return cinemaRepository.findAll()
                .stream().map(CinemaSummaryDto::of).toList();
    }

    /** 특정 영화관의 상세 정보 조회 */
    public CinemaDetailDto getCinema(Long cinemaId) {

        Cinema cinema = cinemaRepository.findByIdWithAuditoriums(cinemaId)
                // custom error handler 수정 필요
                .orElseThrow(() -> new IllegalArgumentException("영화관을 찾을 수 없습니다."));

        List<AuditoriumDto> auditoriums = cinema.getAuditoriums()
                .stream().map(AuditoriumDto::from).toList();

        return CinemaDetailDto.of(cinema, auditoriums);
    }


}
