package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class MemberFormDTO {

    private Long memNum;

    @NotBlank
    @Email(message = "이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min=8, message = "비밀번호는 최소 8자 이상입니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    public MemberFormDTO(Map<String, String> form) {
        this.email = form.get("email");
        this.password = form.get("password");
        this.name = form.get("name");
        this.address = form.get("address");
    }

}
