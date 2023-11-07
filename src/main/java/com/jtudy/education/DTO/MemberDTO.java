package com.jtudy.education.DTO;

import com.jtudy.education.constant.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long memNum;

    private String email;

    private String name;

    private String address;

    private List<Roles> rolesList;

    private LocalDateTime joinedDate;

    private LocalDateTime createdAt;

}