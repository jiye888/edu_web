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

    private String intro;

    private String shortLocation;

    private String managerEmail;

    private String managerName;

    private Double grade;

    private Long reviewCount;

    public AcademyDTO(Academy academy, Double grade, Long count) {
        this.acaNum = academy.getAcaNum();
        this.acaName = academy.getAcaName();
        this.subject = academy.getSubject();
        this.location = academy.getLocation();
        String[] locationSplit = this.location.split("\\s");
        if(locationSplit.length > 2) {
            this.shortLocation = locationSplit[1]+" "+locationSplit[2];
        }
        this.intro = academy.getIntroduction();
        this.managerEmail = academy.getManager().getEmail();
        this.managerName = academy.getManager().getName();
        this.grade = grade;
        this.reviewCount = count;
    }

}
