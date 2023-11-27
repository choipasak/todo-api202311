package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {

    // 이메일로 회원 정보 조회
    // Optional로 안전하게 정보를 받겠다!
    Optional<User> findByEmail(String email); // 쿼리 메서드

    // 이메일 중복 체크
//    @Query("SELECT COUNT(*) FROM User u WHERE u.email =: email") // JPQL
    boolean existsByEmail(String email); // 사실 존재함 ㅋㅋ
}
