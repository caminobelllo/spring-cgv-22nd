package com.ceos22.cgv_clone.domain.movie.controller;

import com.ceos22.cgv_clone.domain.movie.dto.MovieDto;
import com.ceos22.cgv_clone.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    /** 영화 목록 전체 조회 */
    @GetMapping("/movies")
    public List<MovieDto> getMovieList() {
        return movieService.getMovieList();
    }

    /** 영화 상세 조회 */
    @GetMapping("/movies/{movieId}")
    public MovieDto getMovieDetail(@PathVariable Long movieId) {
        return movieService.getMovie(movieId);
    }
}
