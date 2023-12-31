package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoCreateRequestDTO {

    // title이 json객체로 포장되어서 올 것이다.
    @NotBlank
    @Size(min = 2, max = 30)
    private String title;
    
    // dto -> Entity로 변환하는 메서드
    public Todo toEntity(User user){
        return Todo.builder()
                .title(this.title)
                .user(user)
                .build();
    }



}
