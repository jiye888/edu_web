package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long memNum;

    @NotBlank
    @Email
    private String email;

    /*
    @NotBlank
    @Size(min=8)
    private String password;
     */

    @NotBlank
    private String name;

    @NotBlank
    private String address;

}
