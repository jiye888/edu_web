package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImgArrayDTO {

    private String name;

    private String base64;

    private String preText;

    private String postText;

    private Integer textIndex;

    private Integer arrayIndex;

    private String duplicate;

}
