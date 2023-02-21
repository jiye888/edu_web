package com.jtudy.education.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.constant.SubjectConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class AcademyFormDTO {

    @JsonProperty("number")
    private Long acaNum;

    @NotBlank(message = "학원명을 입력해주세요.")
    private String acaName;

    @NotNull(message = "학원에서 수강할 수 있는 과목을 선택해주세요.")
    private EnumSet<Subject> subject;

    @NotBlank(message = "학원의 위치를 입력해주세요.")
    private String location;
/*
    public AcademyFormDTO(Map<String, Object> form) {
        ArrayList<String> subjects = (ArrayList<String>) form.get("subject");
        EnumSet<Subject> subject = EnumSet.noneOf(Subject.class);
        for (String s : subjects) {
            subject.add(Subject.valueOf(s));
        }
        this.acaNum = Long.valueOf(form.get("acaNum").toString());
        this.acaName = form.get("acaName").toString();
        this.subject = subject;
        this.location = form.get("location").toString();
    }*/

}
