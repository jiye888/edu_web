package com.jtudy.education.service;

import com.jtudy.education.DTO.NoticeDTO;
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
    public Long register(NoticeDTO noticeDTO) {
        Notice notice = dtoToEntity(noticeDTO);
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public Long update(NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.getReferenceById(noticeDTO.getNotNum());
        notice.changeNotice(noticeDTO.getTitle(), noticeDTO.getContent());
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public void delete(Long notNum) {
        noticeRepository.deleteById(notNum);
    }
}
