package com.ceos22.cgv_clone.domain.like.repository;

import com.ceos22.cgv_clone.domain.like.entity.CinemaLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CinemaLikeRepository extends JpaRepository<CinemaLike, Long> {

    boolean existsByMemberIdAndCinemaId(Long memberId, Long cinemaId);

    Optional<CinemaLike> findByMemberIdAndCinemaId(Long memberId, Long cinemaId);

    long countByCinemaId(Long cinemaId);

    void deleteByMemberIdAndCinemaId(Long memberId, Long cinemaId);
}
