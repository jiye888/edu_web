package com.jtudy.education.service;

import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeService {

    Page<NoticeDTO> getAll();

    NoticeDTO getOne(Long notNum);

    Long register(NoticeFormDTO noticeFormDTO, Long acaNum);

    Long update(NoticeFormDTO noticeFormDTO);

    void delete(Long notNum);

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
                .acaNum(noticeFormDTO.getAcaNum())
                .build();

        Notice notice = Notice.builder()
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
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .build();

        return noticeDTO;
    }

}
