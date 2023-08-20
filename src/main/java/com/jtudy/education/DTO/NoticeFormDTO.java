package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeFormDTO {

    private Long number;

    @NotNull
    private Long academy;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private String content;

}
