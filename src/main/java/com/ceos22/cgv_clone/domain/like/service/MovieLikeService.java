package com.ceos22.cgv_clone.domain.like.service;

import com.ceos22.cgv_clone.domain.common.dto.CountResponse;
import com.ceos22.cgv_clone.domain.like.dto.MovieLikeRequestDto;
import com.ceos22.cgv_clone.domain.like.entity.MovieLike;
import com.ceos22.cgv_clone.domain.like.repository.MovieLikeRepository;
import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.domain.movie.entity.Movie;
import com.ceos22.cgv_clone.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieLikeService {

    private final MovieLikeRepository movieLikeRepository;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void likeMovie(MovieLikeRequestDto requestDto) {
        Movie movie = movieRepository.findById(requestDto.getMovieId()).orElseThrow();

        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow();

        if (movieLikeRepository.existsByMemberIdAndMovieId(member.getId(), movie.getId())) return;

        try {
            movieLikeRepository.save(MovieLike.create(member, movie));
        } catch (DataIntegrityViolationException ignore) { }
    }

    @Transactional
    public void unlikeMovie(MovieLikeRequestDto requestDto) {
        movieLikeRepository.deleteByMemberIdAndMovieId(requestDto.getMemberId(), requestDto.getMovieId());
    }


    public CountResponse countMovieLikes(Long movieId) {
        return CountResponse.of(movieLikeRepository.countByMovieId(movieId));
    }

    }
