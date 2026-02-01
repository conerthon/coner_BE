package com.example.Traveler.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users") // H2 DB 예약어와 충돌 방지
public class User {

    // 특정 ID만 가진 객체를 생성하기 위한 생성자
    public User(Long id) {
        this.id = id;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;
}
