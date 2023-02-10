package com.jtudy.education.DTO;

import com.jtudy.education.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewFormDTO {

    private Long number;

    private Long academy;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Integer grade;

}
