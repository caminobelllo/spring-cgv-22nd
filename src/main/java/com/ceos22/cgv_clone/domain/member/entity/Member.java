package com.ceos22.cgv_clone.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    // 가입 시 이메일 @앞부분으로 자동 설정
    @Column(nullable = false, length = 20)
    private String nickname;
}
