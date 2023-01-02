package com.jtudy.education.DTO;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class LoginFormDTO {

    private String email;

    @Size(min = 8, message = "비밀번호는 8자 이상입니다.")
    private String password;

}
