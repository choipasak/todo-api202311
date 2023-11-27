package com.example.todo.userapi.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter @Getter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_user")
public class User
{

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id; // 계정명이 아니라 식별 코드로 사용

    @Column(nullable = false, unique = true) // 중복 발생 x, 널값x
    private String email;

    @Column(nullable = false)
    private String passWord;

    @Column(nullable = false)
    private  String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;

}