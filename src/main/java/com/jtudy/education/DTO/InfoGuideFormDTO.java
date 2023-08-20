package com.jtudy.education.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoGuideFormDTO {

    @JsonProperty("number")
    private Long infoNum;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private String content;

}
