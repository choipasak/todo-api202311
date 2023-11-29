package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // (설계 단계에서 완성이 되어 있어야 함)GET: /api/auth/check?email=zzzz@xxx.com
    @GetMapping("/check")
    public ResponseEntity<?> check(String email){ // requestparam 아노테이션 생략한거임
        if(email.trim().equals("")){
            return ResponseEntity.badRequest().body("이메일이 없습니다!");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("{} 중복? - {}", email, resultFlag);

        return ResponseEntity.ok().body(resultFlag);
    }

    // 회원 가입 요청 처리
    // POST: /api/auth
    @PostMapping
    public  ResponseEntity<?> signup(
            @Validated @RequestBody UserRequestSignUpDTO dto,
            BindingResult result
            ){
        log.info("/api/auth POST! - {}", dto);

        if(result.hasErrors()){
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }
        try {
            UserSignUpResponseDTO responseDTO = userService.create(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.info("이메일 중복 일어남!");
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인 요청 처리
    @PostMapping("/signin")
    public  ResponseEntity<?> signIn(
            @Validated @RequestBody LoginRequestDTO dto
    ){

        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    
    // 일반 회원을 프리미엄 회원으로 승격하는 요청 처리
    // 기존의 등급을 올리는 것은 COMMOM에서 PREMIUM으로 수정 해주는 것.
    @PutMapping("/promote")
    // 권한 검사 (해당 권한이 아니라면 인가처리 거부 -> 403 코드 리턴)
    // 메서드 호출 전에 권한 검사 -> 요청 당시 토큰에 있는 user정보가 ROLE_COMMON이라는 권한을 가지고 있는지 검사.
    @PreAuthorize("hasRole('ROLE_COMMON')")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo
            ){
        log.info("/api/auth/promote PUT!");




        try {
            // 로그인 성공하면 토큰을 만들어 줬음 -> 이 토큰은 로그인 한 당시의 토큰 정보임
            // 사용자가 사용 중에 프리미엄으로 등급을 올려주면 등급이 바꼈기 때문에 새로운 사용자의 정보를 담은 토큰이 필요
            // 그래서 새로운 토큰을 생성해서 전달
            // token이 들어있는 DTO(LoginResponseDTO)를 사용 + 새로운 토큰(승격된 등급의 정보를 가지고 있는)
            LoginResponseDTO responseDTO = userService.promoteToPremium(userInfo);

            return ResponseEntity.ok().body(responseDTO);
        }catch (NoRegisteredArgumentException | IllegalArgumentException e){
            // 예상 가능한 예외 (직접 생성하는 예외 처리)
            e.printStackTrace();
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            log.warn(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());

        }
    }

}
