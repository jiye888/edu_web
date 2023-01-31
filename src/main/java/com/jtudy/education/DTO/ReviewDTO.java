package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private Long revNum;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long acaNum;

    private String acaName;

    private Long writerNum;

    private String writerName;

    private String writerEmail;

    @Size(min=1, max=5)
    private Integer grade;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
