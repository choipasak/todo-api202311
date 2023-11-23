package com.example.todo.todoapi.service;

import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    @Autowired
    private final TodoRepository todoRepository;



}
