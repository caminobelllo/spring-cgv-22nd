package com.ceos22.cgv_clone.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieLikeRequestDto {

    private Long memberId;
    private Long movieId;
}
