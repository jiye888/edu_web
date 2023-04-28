package com.jtudy.education.DTO;

import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO {

    private Long fileId;

    private String originalName;

    private String fileName;

    private String fileType;

    private String filePath;

    private String fileSize;

    private Member uploader;

}
