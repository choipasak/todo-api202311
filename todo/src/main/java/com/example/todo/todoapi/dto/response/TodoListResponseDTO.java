package com.example.todo.todoapi.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListResponseDTO {

    // 할 일 객체들 모여 있는 목록

    // 에러 발생 시 에러 메세지를 담을 필드
    private String error;

    private List<TodoDetailResponseDTO> todos;


}
