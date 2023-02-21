package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Notice;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.NoticeRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final AcademyRepository academyRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long acaNum, SecurityMember member) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        return academy.getCreatedBy().equals(member.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> getAll(Long acaNum, Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "notNum"));
        Page<Notice> notice = noticeRepository.findByAcaNum(pageable, acaNum);
        Page<NoticeDTO> noticeDTO = notice.map(e -> entityToDTO(e));
        return noticeDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDTO getOne(Long notNum) {
        Notice notice = noticeRepository.findByNotNum(notNum);
        NoticeDTO noticeDTO = entityToDTO(notice);
        return noticeDTO;
    }

    @Override
    public Long register(NoticeFormDTO noticeFormDTO, Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Notice notice = formToEntity(noticeFormDTO);
        notice.builder().academy(academy).build();
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public Long update(NoticeFormDTO noticeFormDTO) {
        Notice notice = noticeRepository.findByNotNum(noticeFormDTO.getNumber());
        notice.changeNotice(noticeFormDTO.getTitle(), noticeFormDTO.getContent());
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public void delete(Long notNum) {
        noticeRepository.deleteById(notNum);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> search(String category, String keyword, Pageable pageable) {
        Page<Notice> notice = null;
        if (category.equals("title")) {
            notice = noticeRepository.findByTitleContaining(keyword, pageable);
        } else if (category.equals("content")) {
            notice = noticeRepository.findByContentContaining(keyword, pageable);
        } else if (category.equals("title&content")) {
            notice = noticeRepository.findByTitleOrContentContaining(keyword, keyword, pageable);
        }
        Page<NoticeDTO> noticeDTO = notice.map(e -> entityToDTO(e));
        return noticeDTO;
    }
}
