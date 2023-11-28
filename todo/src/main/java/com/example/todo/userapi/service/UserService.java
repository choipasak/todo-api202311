package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider; // 이렇게 사용하려고 TokenProvider클래스에 @Component를 작성 해 줬었다.

    //
    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto) {
        String email = dto.getEmail();

        if(isDuplicate(email)){ // 중복 된 값을 확인 후 true, false 값 리턴
            log.warn("이메일이 중복되었습니다 - {}", email);
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword()); // getPassword() 뽑아서 값 암호화 하고
        dto.setPassword(encoded); // dto에 암호화 비번 set하기
        
        // dto를 User Entity로 변환해서 저장
        User saved = userRepository.save(dto.toEntity()); // dto를 Entity화 시키기
        log.info("회원 가입 정상 수행됨! - saved user - {}", saved);


        return new UserSignUpResponseDTO(saved);
    }

    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 인증
    public LoginResponseDTO authenticate(final LoginRequestDTO dto){
        // 이메일을 통해 회원 정보 조회
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new RuntimeException("가입된 회원이 아닙니다.")
        );

        // 패스워드 검증

        String rawPassword = dto.getPassword();// 입력한 비번
        String encodedPassword = user.getPassWord(); // DB에 저장 된 암호화 된 비번

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {

            throw  new RuntimeException("비밀번호가 틀렸습니다.");

        }
        log.info("{}님 로그인 성공!", user.getUserName());
        // 여기까지가 로그인 성공.

        // 로그인 성공 후에 클라에게 뭘 리턴할 것인가?
        // -> JWT를 클라에게 새롭게 발급해 주어야 한다! (목표)
        String token = tokenProvider.createToken(user); // 유저 정보를 넘기고 맞는 토큰 생성 완료
        // 만든 토큰을 클라에게 전달(dto를 통해)

        return new LoginResponseDTO(user, token);

    }


}
