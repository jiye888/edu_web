package com.jtudy.education.service;

import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.FileUploadRepository;
import com.jtudy.education.repository.NoticeRepository;
import com.jtudy.education.repository.specification.NoticeSpecification;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final AcademyRepository academyRepository;
    private final ImageService imageService;
    private final FileUploadService fileUploadService;
    private final FileUploadRepository fileUploadRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long acaNum, SecurityMember member) {
        try {
            Academy academy = academyRepository.findByAcaNum(acaNum);
            return academy.getCreatedBy().equals(member.getUsername()) || member.getMember().getRolesList().contains(Roles.ADMIN);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> getAll(Long acaNum, Pageable pageable) {
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
/*
    public void uploadImages(MultipartFile[] files, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        for (MultipartFile file : files) {
            Image image = imageService.fileToEntity(file, member);
            image.setNotice(notice);
            imageService.uploadImage(image, member);
        }
    }*/

    public void uploadFiles(MultipartFile[] files, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
            if (!fileUploadService.isValidExtension(extension)) {
                throw new IOException("유효한 파일 형식이 아닙니다.");
            }

            FileUpload fileUpload = fileUploadService.fileToEntity(file, member);
            fileUpload.setNotice(notice);
            fileUploadRepository.save(fileUpload);
            notice.addFile(fileUpload);
            noticeRepository.save(notice);
            fileUploadService.uploadFile(fileUpload, file);
        }
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
    public Page<NoticeDTO> search(Long acaNum, Map<String, String> map, Pageable pageable) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if(entry.getValue() == null) {
                iterator.remove();
            }
        }

        List<String> categories = new ArrayList<>(map.keySet());
        if (categories.isEmpty()) {
            throw new NullPointerException("검색어를 입력해주세요.");
        }

        Specification<Notice> spec = NoticeSpecification.findByAcademy(acaNum);

        if (categories.size() == 1) {
            if (categories.contains("title")) {
                spec = spec.and(NoticeSpecification.titleContaining(map.get("title")));
            } else if (categories.contains("content")) {
                spec = spec.and(NoticeSpecification.contentContaining(map.get("content")));
            }
        } else if (categories.size() == 2) {
            for (String category : categories) {
                if (category.contains("title")) {
                    spec = spec.or(NoticeSpecification.titleContaining(map.get("title")));
                } else if (category.equals("content")) {
                    spec = spec.or(NoticeSpecification.contentContaining(map.get("content")));
                }
            }
        }

        Page<Notice> notice = noticeRepository.findAll(spec, pageable);
        Page<NoticeDTO> noticeDTO = notice.map(e -> entityToDTO(e));
        return noticeDTO;
    }
}
