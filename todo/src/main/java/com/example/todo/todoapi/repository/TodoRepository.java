package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    // (전체 목록 말고)특정 회원의 할 일 목록을 리턴 해 주자!
    @Query("SELECT t FROM Todo t WHERE t.user = :user") // 변수명과 조건명이 같으니 매개 변수에 @Param생략
    List<Todo> findAllByUser(User user); // 왜 userId아니고 Entity를 받는지: ?

}
