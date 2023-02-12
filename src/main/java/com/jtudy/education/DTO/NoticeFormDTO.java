package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeFormDTO {

    private Long notNum;

    private Long acaNum;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public NoticeFormDTO(Map<String, String> form) {
        if (form.get("number") != null) {
            this.notNum = Long.parseLong(form.get("number"));
        }
        this.acaNum = Long.valueOf(form.get("academy"));
        this.title = form.get("title");
        this.content = form.get("content");
    }

}
