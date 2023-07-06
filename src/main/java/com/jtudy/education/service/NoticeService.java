package com.jtudy.education.service;

import com.jtudy.education.DTO.*;
import com.jtudy.education.entity.*;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NoticeService {

    boolean validateMember(Long acaNum, SecurityMember member);

    Page<NoticeDTO> getAll(Long acaNum, Pageable pageable);

    NoticeDTO getOne(Long notNum);

    List<FileUploadDTO> getAllFiles(Long notNum);

    List<ImageDTO> getAllImages(Long notNum);

    void checkImageName(Long notNum, Image image);

    void checkFileName(Long notNum, FileUpload fileUpload);

    Image setNotice(Image image, Long notNum, ImgArrayDTO imgArray);

    Long register(NoticeFormDTO noticeFormDTO, Long acaNum);

    void registerFile(MultipartFile[] files, Long notNum, Member member) throws IOException;

    void registerImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, Long notNum, Member member) throws IOException;

    void registerImg(MultipartFile image, List<ImgArrayDTO> imgArray, Long notNum, Member member) throws IOException;

    void deleteFile(Long notNum, Long fileId) throws IOException;

    void updateFile(MultipartFile[] files, List<String> existFiles, Long notNum, Member member) throws IOException;

    void updateImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, List<ImgArrayDTO> existImgArray, Long notNum, Member member) throws IOException;

    Long update(NoticeFormDTO noticeFormDTO);

    void delete(Long notNum) throws IOException;

    Page<NoticeDTO> search(Long acaNum, Map<String, String> map, Pageable pageable);

    default Notice dtoToEntity(NoticeDTO noticeDTO) {
        Academy academy = Academy.builder()
                .acaNum(noticeDTO.getAcaNum())
                .build();

        Notice notice = Notice.builder()
                .notNum(noticeDTO.getNotNum())
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .academy(academy)
                .build();

        return notice;
    }

    default Notice formToEntity(NoticeFormDTO noticeFormDTO) {
        Academy academy = Academy.builder()
                .acaNum(noticeFormDTO.getAcademy())
                .build();

        Notice notice = Notice.builder()
                .notNum(noticeFormDTO.getNumber())
                .academy(academy)
                .content(noticeFormDTO.getContent())
                .title(noticeFormDTO.getTitle())
                .build();

        return notice;
    }

    default NoticeDTO entityToDTO(Notice notice) {
        NoticeDTO noticeDTO = NoticeDTO.builder()
                .notNum(notice.getNotNum())
                .title(notice.getTitle())
                .content(notice.getContent())
                .acaNum(notice.getAcademy().getAcaNum())
                .acaName(notice.getAcademy().getAcaName())
                .managerEmail(notice.getCreatedBy())
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .build();

        return noticeDTO;
    }

    List<byte[]> getFilesByte(Long notNum) throws IOException;

    List<byte[]> getImagesByte(Long notNum) throws IOException;
}
