package com.jtudy.education.DTO;

import com.jtudy.education.constant.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class AcademyDTO {

    private Long acaNum;

    private String acaName;

    @NotEmpty
    private EnumSet<Subject> subject;

    @NotEmpty
    private String location;

    private Long managerMemNum;

    private String managerEmail;

    private String managerName;

    private Integer grade;

}
