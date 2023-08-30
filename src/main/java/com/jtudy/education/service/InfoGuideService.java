package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.DTO.InfoGuideDTO;
import com.jtudy.education.DTO.InfoGuideFormDTO;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.InfoGuide;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface InfoGuideService {

    default InfoGuide formToEntity(InfoGuideFormDTO info) {
        InfoGuide infoGuide = InfoGuide.builder()
                .title(info.getTitle())
                .content(info.getContent())
                .build();

        return infoGuide;
    }

    default InfoGuideDTO entityToDTO(InfoGuide infoGuide) {
        InfoGuideDTO infoGuideDTO = InfoGuideDTO.builder()
                .infoNum(infoGuide.getInfoNum())
                .title(infoGuide.getTitle())
                .content(infoGuide.getContent())
                .createdAt(infoGuide.getCreatedAt())
                .modifiedAt(infoGuide.getModifiedAt())
                .build();

        return infoGuideDTO;
    }

    @Transactional(readOnly = true)
    boolean validateMember(SecurityMember member);

    @Transactional(readOnly = true)
    Page<InfoGuideDTO> getAll(Pageable pageable);

    @Transactional(readOnly = true)
    InfoGuideDTO getOne(Long infoNum);

    @Transactional(readOnly = true)
    List<ImageDTO> getAllImages(Long infoNum);

    Long register(InfoGuideFormDTO infoGuideFormDTO, Member member);

    void checkImageName(Long infoNum, Image image);

    Image setInfoGuide(Image image, Long infoNum, ImgArrayDTO imgArray);

    void registerImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, Long infoNum, Member member) throws IOException;

    Long update(InfoGuideFormDTO infoGuideFormDTO);

    void updateImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, List<ImgArrayDTO> existImgArray, Long infoNum, Member member) throws IOException;

    void delete(Long infoNum) throws IOException;

    void removeAllImg(Long infoNum) throws IOException;

    void removeImg(Long imageId, Long infoNum) throws IOException;

    @Transactional(readOnly = true)
    Page<InfoGuideDTO> search(Map<String, String> map, Pageable pageable);

}
