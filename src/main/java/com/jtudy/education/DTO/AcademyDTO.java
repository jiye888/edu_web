package com.jtudy.education.DTO;

import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.EnumSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademyDTO {

    private Long acaNum;

    private String acaName;

    private EnumSet<Subject> subject;

    private String location;

    private Long managerMemNum;

    private String managerEmail;

    private String managerName;

    private Double grade;

    private Long reviewCount;

    public AcademyDTO(Academy academy, Double grade, Long count) {
        this.acaNum = academy.getAcaNum();
        this.acaName = academy.getAcaName();
        this.subject = academy.getSubject();
        this.location = academy.getLocation();
        this.grade = grade;
        this.reviewCount = count;
    }


}
