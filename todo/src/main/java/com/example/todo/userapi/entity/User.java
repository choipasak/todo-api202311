package com.example.todo.userapi.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id; // 계정명이 아니라 식별 코드

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
//    @ColumnDefault("'COMMON'")
    @Builder.Default
    private Role role = Role.COMMON; // 유저 권한

    private String profileImg; // 프로필 이미지 경로

    private String accessToken;; // 카카오 로그인 시 발급받는 accessToken을 저장 -> 로그아웃 때 필요

    // 등급 수정 메서드
    public void changeRole(Role role){
        this.role = role;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

}

//package com.example.todo.userapi.entity;
//
//import lombok.*;
//import org.hibernate.annotations.ColumnDefault;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Getter
//@ToString @EqualsAndHashCode(of = "id")
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//
//@Entity // Entity는 @Setter가 없을 수 있
//@Table(name = "tbl_user")
//public class User
//{
//
//    @Id
//    @Column(name = "user_id")
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    private String id; // 계정명이 아니라 식별 코드로 사용
//
//    @Column(nullable = false, unique = true) // 중복 발생 x, 널값x
//    private String email;
//
//    @Column(nullable = false)
//    private String passWord;
//
//    @Column(nullable = false)
//    private  String userName;
//
//    @CreationTimestamp
//    private LocalDateTime joinDate;
//
//    @Enumerated(EnumType.STRING) // EnumType.ORDINAL: DB에 0, 1, 2 같이 값이 들어간다.
//    // @ColumnDefault("'COMMON'") // 안쪽의 '' 이유: ENUM타입이어서. | 주석 처리 이유: DB에 null로 들어감
//    @Builder.Default
//    private Role role = Role.COMMON; // 유저 권한 등급
//
//    // 등급 수정 메서드
//    public void  changeRole(Role role){
//        this.role = role;
//    }
//}
