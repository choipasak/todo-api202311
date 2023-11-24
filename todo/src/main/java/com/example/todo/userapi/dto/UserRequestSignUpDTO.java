package com.example.todo.userapi.dto;

import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter @Getter
@EqualsAndHashCode(of = "email")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestSignUpDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;

    // dto->Entity 변경해주는 toEntity 메서드 작성
    public User toEntity(){
        return User.builder()
                .email(this.email)
                .passWord(this.password)
                .userName(this.userName)
                .build();
    }
}
