package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long memNum;

    private String email;

    private String name;

    private String address;

    private LocalDateTime joinedDate;

    private LocalDateTime createdAt;

}
