package com.example.todo.todoapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
@CrossOrigin
public class TodoController {

    private final TodoService todoService;

    // 할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestBody TodoCreateRequestDTO requestDTO,
            BindingResult result // json 객체로 받은 결과를 담는 객체 / 검증 오류를 보관 하는 객체다.
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
            TodoListResponseDTO responseDTO = todoService.create(requestDTO, userInfo);
            return ResponseEntity.ok().body(responseDTO); // 서비스에서 받은 TodoListResponseDTO의 결과
        } catch (IllegalArgumentException e){
            // 권한 때문에 발생한 예외
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder() // 객체를 생성 해야해서 빌더를 부름
                            .error(e.getMessage()) // 클래스에 error라는 필드가 있음 매개 값으로 에러메세지를 담아서 리턴
                            .build());
        }
    }

    // 페이지에 들어 오면 할 일 목록을 띄워 주기 위해 들어 오는 요청
    // put: 통채로 하는 느낌
    // fetch: 한 부분만 하는 느낌
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(
            // JwtAuthFilter에서 시큐리티에게 전역적으로 사용할 수 있는 인증 정보를 등록해 놓았기 때문에
            // @AuthenticationPrincipal을 통해 토큰에 인증된 사용자 정보를 불러올 수 있다.
            @AuthenticationPrincipal TokenUserInfo userInfo
            ){
        log.info("/api/todos GET request");

        TodoListResponseDTO responseDTO = todoService.retrieve(userInfo.getUserId());

        return ResponseEntity.ok().body(responseDTO);
    }

    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") String todoId
    ){
        log.info("/api/todos/{} DELETE request!");

        if(todoId == null || todoId.trim().isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body(TodoListResponseDTO
                            .builder()
                            .error("ID를 전달해 주세요")
                            .build());
        }

        try {
            TodoListResponseDTO responseDTO = todoService.delete(todoId, userInfo.getUserId());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TodoListResponseDTO.builder().error(e.getMessage()).build());
        }
    }

    // 할 일 수정하기
    // 할 일 완료인지 아닌지를 체크하는 메서드
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<?> updateTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody @Validated TodoModifyRequestDTO requestDTO, // NotBlank가 있음
            BindingResult result,
            HttpServletRequest request // 요청 방식이 PATCH인지 PUT인지 확인 용도
    ){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        log.info("/api/todos {} request!", request.getMethod());
        log.info("modifying dto: {}", requestDTO);

        try {
            TodoListResponseDTO responseDTO = todoService.update(requestDTO, userInfo.getUserId());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TodoListResponseDTO.builder().error(e.getMessage()).build());
        }
    }
}
