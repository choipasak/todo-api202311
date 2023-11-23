package com.example.todo.todoapi.dto.response;


import com.example.todo.todoapi.entity.Todo;
import lombok.*;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDetailResponseDTO {
    
    private String id;

    private String title;

    private boolean done;
    
    // Entity -> DTO 형태로 변환해주는 생성자
    public TodoDetailResponseDTO(Todo todo) {
        this.id = todo.getTodoId();
        this.title = todo.getTitle();
        this.done = todo.isDone(); // boolean타입은 getter가 is로 시작한다.
    }
}
