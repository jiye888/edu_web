package com.jtudy.education.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jtudy.education.constant.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;

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

    private String intro;

}
