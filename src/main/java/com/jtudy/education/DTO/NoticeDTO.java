package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {

    private Long notNum;

    private String title;

    private String content;

    private String managerEmail;

    private Long acaNum;

    private String acaName;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}