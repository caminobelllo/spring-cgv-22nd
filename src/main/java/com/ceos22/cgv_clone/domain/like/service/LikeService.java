package com.ceos22.cgv_clone.domain.like.service;

import com.ceos22.cgv_clone.domain.like.dto.LikeToggleResponseDto;
import com.ceos22.cgv_clone.domain.like.entity.CinemaLike;
import com.ceos22.cgv_clone.domain.like.entity.MovieLike;
import com.ceos22.cgv_clone.domain.like.repository.CinemaLikeRepository;
import com.ceos22.cgv_clone.domain.like.repository.MovieLikeRepository;
import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.domain.movie.entity.Movie;
import com.ceos22.cgv_clone.domain.movie.repository.MovieRepository;
import com.ceos22.cgv_clone.domain.theater.entity.Cinema;
import com.ceos22.cgv_clone.domain.theater.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final MovieLikeRepository movieLikeRepository;
    private final CinemaLikeRepository cinemaLikeRepository;
    private final MemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;

    // 영화 찜
    @Transactional
    public LikeToggleResponseDto toggleMovieLike(Long memberId, Long movieId) {

        Optional<MovieLike> movieLikeOptional = movieLikeRepository.findByMemberIdAndMovieId(memberId, movieId);

        boolean isLiked;

        // 이미 좋아요 한 경우
        if (movieLikeOptional.isPresent()) {
            movieLikeRepository.delete(movieLikeOptional.get());
            isLiked = false;
        }
        else {
            Member member = memberRepository.findById(memberId).orElseThrow(/* MemberNotFoundException */);
            Movie movie = movieRepository.findById(movieId).orElseThrow(/* MovieNotFoundException */);
            movieLikeRepository.save(MovieLike.create(member, movie));
            isLiked = true;
        }

        long likeCount = movieLikeRepository.countByMovieId(movieId);
        return new LikeToggleResponseDto(isLiked, likeCount);
    }

    // 영화관 찜
    @Transactional
    public LikeToggleResponseDto toggleCinemaLike(Long memberId, Long cinemaId) {
        Optional<CinemaLike> cinemaLikeOptional = cinemaLikeRepository.findByMemberIdAndCinemaId(memberId, cinemaId);

        boolean isLiked;

        if (cinemaLikeOptional.isPresent()) {
            cinemaLikeRepository.delete(cinemaLikeOptional.get());
            isLiked = false;
        } else {
            Member member = memberRepository.findById(memberId).orElseThrow(/* MemberNotFoundException */);
            Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow(/* CinemaNotFoundException */);
            cinemaLikeRepository.save(CinemaLike.create(member, cinema));
            isLiked = true;
        }

        long likeCount = cinemaLikeRepository.countByCinemaId(cinemaId);
        return new LikeToggleResponseDto(isLiked, likeCount);
    }
}
