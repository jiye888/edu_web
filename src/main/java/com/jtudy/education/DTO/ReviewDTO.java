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

    private String title;

    private String content;

    private Long acaNum;

    private String acaName;

    private String writerName;

    private String writerEmail;

    private Integer grade;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
