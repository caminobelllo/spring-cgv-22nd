package com.ceos22.cgv_clone.domain.member.service;

import com.ceos22.cgv_clone.domain.member.dto.SignUpRequestDto;
import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.global.apiPayload.code.error.ErrorCode;
import com.ceos22.cgv_clone.global.apiPayload.exception.CustomException;
import com.ceos22.cgv_clone.global.security.JwtTokenProvider;
import com.ceos22.cgv_clone.global.security.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Member signup(SignUpRequestDto request) {

        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );

        return memberRepository.save(member);
    }

    @Transactional
    public TokenDto login(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        // Authentication 객체 생성
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole().getDescription()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null, authorities);

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createToken(authentication);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

}
