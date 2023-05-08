package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveFileDTO {

    private MultipartFile[] files;

    private MultipartFile[] images;

    private List<String> existFiles;

    private List<List<String>> imgArray;

    private List<List<String>> existImgArray;
}
