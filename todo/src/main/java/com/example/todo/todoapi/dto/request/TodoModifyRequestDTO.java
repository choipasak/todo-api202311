package com.example.todo.todoapi.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoModifyRequestDTO {
    // 체크박스 눌렀을 때 done의 상태를 변경 해주는 클래스

    @NotBlank
    private String id;
    private boolean done;
    
}
