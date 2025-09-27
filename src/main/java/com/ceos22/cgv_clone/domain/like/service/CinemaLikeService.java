package com.ceos22.cgv_clone.domain.like.service;

import com.ceos22.cgv_clone.domain.common.dto.CountResponse;
import com.ceos22.cgv_clone.domain.like.dto.CinemaLikeRequestDto;
import com.ceos22.cgv_clone.domain.like.entity.CinemaLike;
import com.ceos22.cgv_clone.domain.like.repository.CinemaLikeRepository;
import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.domain.theater.entity.Cinema;
import com.ceos22.cgv_clone.domain.theater.repository.CinemaRepository;
import com.ceos22.cgv_clone.global.apiPayload.code.error.ErrorCode;
import com.ceos22.cgv_clone.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaLikeService {

    private final CinemaLikeRepository cinemaLikeRepository;
    private final CinemaRepository cinemaRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void likeCinema(CinemaLikeRequestDto requestDto) {

        Cinema cinema = cinemaRepository.findById(requestDto.getCinemaId())
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() ->  new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (cinemaLikeRepository.existsByMemberIdAndCinemaId(member.getId(), cinema.getId())) return;

        try {
            cinemaLikeRepository.save(CinemaLike.create(member, cinema));
        } catch (DataIntegrityViolationException ignore) { }
    }

    @Transactional
    public void unlikeCinema(CinemaLikeRequestDto requestDto) {
        cinemaLikeRepository.deleteByMemberIdAndCinemaId(requestDto.getMemberId(), requestDto.getCinemaId());
    }

    public CountResponse countCinemaLikes(Long cinemaId) {
        return CountResponse.of(cinemaLikeRepository.countByCinemaId(cinemaId));
    }

}
