package com.example.todo.auth;


import com.example.todo.userapi.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component // 해당 하는 빈이 없지만 빈 등록 하고 싶을 때!
@Slf4j
// 역할: 토큰을 발급하고, 서명 위조를 검사하는 객체.
public class TokenProvider {

    // 서명에 사용할 값 (512비트 이상의 랜덤 문자열)
    // @Value: properties 형태의 파일의 내용을 읽어서 변수에 대입하는 아노테이션.(yml도 가능)
    @Value("${jwt.secret}") // 토큰의 키 값(야믈파일에 적어 놓은 이름)을 주입 받는 아노테이션.
    private String SECRET_KEY;
    
    // 토큰 생성 메서드

    /**
     * JSON Web Token을 생성하는 메서드
     * @param userEntity - 토큰의 내용(클레임)에 포함 될 유저 정보
     * @return - 생성 된 JSON을 암호화 한 토큰 값
     */
    public  String createToken(User userEntity){

        // 토큰 만료시간 생성
        Date expiry = Date.from(
                // LocalDateTime으로 만들어야 했지만 귀찮아서 그냥 Date객체를 사용 함.
                // 밑의 setExpiration()가 자바 Util의 Date객체만 받을 수 있음
                Instant.now().plus(1, ChronoUnit.DAYS)
                // 하루 짜리 토큰임. 다음 날이면 만료 되서 소멸 된다!
        );

        // 토큰 생성
        /*
        - 토큰의 내용 형태 예시 (토큰의 내용 = 클레임)
            {
                "iss": "서비스 이름(발급자)",
                "exp": "2023-12-27(만료 일자)",
                "iat": "2023-11-27(발급 일자)"
                "email": "로그인 한 사람 이메일",
                "role": "Premium",
                ...
                == 서명
            }
         */

        // 추가 클레임 정의
        Map<String, String> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        claims.put("role", userEntity.getRole().toString()); // ROLE이라는 ENUM타입의 문자열이어서 변환을 해줘야 한다 -> toString()

        return Jwts.builder()
                // 1번째로 들어가야 할 값: token header에 들어갈 서명
                .signWith( // 1빠! 선언
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                // token payload에 들어갈 클레임(토큰의 내용) 설정.
                .setClaims(claims) // 추가 클레임은 먼저(서명 다음) 설정해야 함, 마지막에 설정하면 에러남
                .setIssuer("Todo운영자") // iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급 시간
                .setExpiration(expiry) // exp: 만료 시간
                .setSubject(userEntity.getId()) // subject: 토큰을 식별할 수 있는 주요 데이터, Id가 PK여서 지정
                .compact(); // 압축
    }

}
