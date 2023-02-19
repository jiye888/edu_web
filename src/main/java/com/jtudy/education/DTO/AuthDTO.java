package com.jtudy.education.DTO;

import com.jtudy.education.constant.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {

    private String email;
    private boolean processed;
    private Roles roles;

}
