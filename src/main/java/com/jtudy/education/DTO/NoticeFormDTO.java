package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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

}
