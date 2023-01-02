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

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long managerNum;

    private String managerName;

    private String managerEmail;

    private Long acaNum;

    private String acaName;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
