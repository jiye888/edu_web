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

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    public NoticeFormDTO(Map<String, String> form) {
        this.acaNum = Long.valueOf(form.get("academy"));
        this.title = form.get("title");
        this.content = form.get("content");
    }

}
