package com.jtudy.education.DTO;

import com.jtudy.education.constant.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.EnumSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class AcademyFormDTO {

    private String acaName;

    @NotBlank
    private EnumSet<Subject> subject;

    @NotBlank
    private String location;

}
