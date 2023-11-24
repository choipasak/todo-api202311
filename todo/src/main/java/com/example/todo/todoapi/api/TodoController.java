package com.example.todo.todoapi.api;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    // 할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(
            @Validated @RequestBody TodoCreateRequestDTO requestDTO,
            BindingResult result // json객체로 받은 결과를 담는 객체 / 검증오류를 보관하는 객체다.
    ){

        if(result.hasErrors()){
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
        return ResponseEntity
                .badRequest()
                .body(result.getFieldError());
        }

        // 에러나지 않고 여기로 왔다면
        // TodoListResponseDTO -> 글 하나를 담는 DTO객체
        try {
            // create메서드 내부의 save메서드가 에러를 발생 시킬 수도 있어서 컨트롤러에서 받아줌
            TodoListResponseDTO responseDTO = todoService.create(requestDTO);
            return ResponseEntity.ok().body(responseDTO); // 서비스에서 받은 TodoListResponseDTO의 결과
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder() // 객체를 생성 해야해서 빌더를 부름
                            .error(e.getMessage()) // 클래스에 error라는 필드가 있음 매개 값으로 에러메세지를 담아서 리턴
                            .build());
        }
    }

    // 페이지에 들어오면 할 일 목록을 띄워주기 위해 들어오는 요청
    // put: 통채로 하는느낌
    // fetch: 한 부분만 하는 느낌
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(){
        log.info("/api/todos GET request");

        TodoListResponseDTO responseDTO = todoService.retrieve();

        return ResponseEntity.ok().body(responseDTO);
    }


}
