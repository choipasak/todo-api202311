package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoService {

    @Autowired
    private final TodoRepository todoRepository;


    // 매개 변수에 final -> 컨트롤러가 전달해준 값을 서비스에서 변경할 수 없다. 불변값이다
    public TodoListResponseDTO create(final TodoCreateRequestDTO requestDTO) throws Exception{

        // 입력한 게시글 등록
        todoRepository.save(requestDTO.toEntity()); // save가 에러를 발생시킬 수도 있음
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());

        return retrieve();
    }

    // 글 전체 목록 조회해서 가져온 후 리턴하는 메서드 (2번 이상 사용해서 메서드로 추출)
    // 컨트롤러에서 페이지 들어오면 바로 글 목록 띄워주는(GET요청) 기능을 서비스에서 메서드로 사용 가능(private -> public)
    public TodoListResponseDTO retrieve() {
        List<Todo> entityList = todoRepository.findAll(); // (페이징 없는)전체 목록 조회 결과
        // Todo 엔터티를 TodoListResponseDTO로 변경해 줘야함. 그래야 TodoListResponseDTO에 담아서 리턴이 가능해짐
        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                // .map(todo -> new TodoListResponseDTO(todo))
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();
    }

}
