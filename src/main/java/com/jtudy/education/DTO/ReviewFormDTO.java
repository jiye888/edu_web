package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewFormDTO {

    private Long revNum;

    private Long acaNum;

    private Long memNum;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Size(min=1, max=5)
    private Integer grade;

}
