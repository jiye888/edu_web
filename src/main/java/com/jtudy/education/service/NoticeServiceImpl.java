package com.jtudy.education.service;

import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Notice;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final AcademyRepository academyRepository;

    @Override
    public Page<NoticeDTO> getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "notNum"));
        Page<Notice> notice = noticeRepository.findAll(pageable);
        Page<NoticeDTO> noticeDTO = notice.map(e -> entityToDTO(e));
        return noticeDTO;
    }

    @Override
    public NoticeDTO getOne(Long notNum) {
        Notice notice = noticeRepository.findByNotNum(notNum);
        NoticeDTO noticeDTO = entityToDTO(notice);
        return noticeDTO;
    }

    @Override
    public Long register(NoticeFormDTO noticeFormDTO, Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Notice notice = formToEntity(noticeFormDTO); // parameter에 academy 추가
        noticeRepository.save(notice);
        return notice.getNotNum();
    }
    //academy 정보 어떻게 가져올 것인지?
    //register 버튼 누를 때 acaNum 받아오기?

    @Override
    public Long update(NoticeFormDTO noticeFormDTO) {
        Notice notice = noticeRepository.getReferenceById(noticeFormDTO.getNotNum());
        notice.changeNotice(noticeFormDTO.getTitle(), noticeFormDTO.getContent());
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public void delete(Long notNum) {
        noticeRepository.deleteById(notNum);
    }
}
