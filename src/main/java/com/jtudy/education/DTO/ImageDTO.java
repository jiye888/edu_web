package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    private Long imageId;

    private String originalName;

    private String name;

    private String path;

    private Integer order;

    private String index;

    private String uploader;

}
