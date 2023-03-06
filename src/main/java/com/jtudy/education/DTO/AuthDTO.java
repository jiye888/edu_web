package com.jtudy.education.DTO;

import com.jtudy.education.constant.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {

    private Long authId;
    private String name;
    private String email;
    private boolean processed;
    private Roles roles;
    private LocalDateTime createdAt;
    private String content;

}
