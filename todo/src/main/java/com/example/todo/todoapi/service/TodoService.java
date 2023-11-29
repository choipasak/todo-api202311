package com.example.todo.todoapi.service;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoService {


    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 매개 변수에 final -> 컨트롤러가 전달해준 값을 서비스에서 변경할 수 없다. 불변 값이다
    public TodoListResponseDTO create(final TodoCreateRequestDTO requestDTO,
                                      final TokenUserInfo userInfo
    ) throws Exception{


        User user = getUser(userInfo.getUserId());
        // - 권한에 따른 글쓰기 제한 처리
        // 일반 회원이 일정을 5개 초과해서 작성하면 예외를 발생.
        if(userInfo.getRole() == Role.COMMON && todoRepository.countByUser(user) >= 5){
            // COMMON은 일정 5개까지만 작성가능, 프리미엄 제안
            throw new IllegalArgumentException("COMMON회원 등급은 더 이상 일정을 작성할 수 없습니다.");
        }

        // 이제는 할 일 등록은 회원만 할 수 있도록 세팅하기 때문에
        // toEntity의 매개 값으로 User 엔터티도 함께 전달해야 합니다 -> userId로 회원 엔터티를 조회해야 함.
        // 입력한 게시글 등록
        todoRepository.save(requestDTO.toEntity(user)); // save가 에러를 발생시킬 수도 있음 + 이제는 user 정보도 필요
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());

        return retrieve(userInfo.getUserId());
    }

    // 글 전체 목록 조회해서 가져온 후 리턴하는 메서드 (2번 이상 사용해서 메서드로 추출)
    // 컨트롤러에서 페이지 들어오면 바로 글 목록 띄워주는(GET요청) 기능을 서비스에서 메서드로 사용 가능(private -> public)
    public TodoListResponseDTO retrieve(String userId) {

        // 로그인 한 유저의 정보를 DB에서 조회
        User user = getUser(userId);

        List<Todo> entityList = todoRepository.findAllByUser(user); // (페이징 없는)전체 목록 조회 결과
        // Todo 엔터티를 TodoListResponseDTO로 변경해 줘야함. 그래야 TodoListResponseDTO에 담아서 리턴이 가능해짐
        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                // .map(todo -> new TodoListResponseDTO(todo))
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    public TodoListResponseDTO delete(final String todoId, final String userId) {
        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
                    , todoId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }

        return retrieve(userId); // (userId)의 글 전체 목록 리턴 메서드
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, final String userId) throws Exception{
        // 조회 하고 저장 하는 방식임
        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());
        targetEntity.ifPresent(todo -> {
            //setter를 이용해서 받은 값을 DB의 done 값에 넣어주기. (뒤집는 처리는 화면단에서 할 것임)
            todo.setDone(requestDTO.isDone());

            todoRepository.save(todo); // 여기서 todo는 조회해온 값 + 수정
        });

        return  retrieve(userId);
    }
}
