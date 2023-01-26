package com.jtudy.education.DTO;

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

    private Long acaNum;

    @NotBlank
    private String acaName;

    @NotNull
    private EnumSet<Subject> subject;

    @NotBlank
    private String location;

    public AcademyFormDTO(Map<String, Object> form) {
        ArrayList<String> subjects = (ArrayList<String>) form.get("subject");
        EnumSet<Subject> subject = EnumSet.noneOf(Subject.class);
        for (String s : subjects) {
            subject.add(Subject.valueOf(s));
        }
        this.acaName = form.get("acaName").toString();
        this.subject = subject;
        this.location = form.get("location").toString();
    }

}
