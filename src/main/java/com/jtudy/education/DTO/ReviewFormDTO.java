package com.jtudy.education.DTO;

import com.jtudy.education.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

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

    public ReviewFormDTO(Map<String, Object> form, Long memNum) {
        this.acaNum = (Long) form.get("academy");
        this.memNum = memNum;
        this.title = form.get("title").toString();
        this.content = form.get("content").toString();
        this.grade = (Integer) form.get("grade");
    }

}
