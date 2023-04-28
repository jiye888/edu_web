package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.FileUploadRepository;
import com.jtudy.education.repository.ImageRepository;
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
    private final ImageRepository imageRepository;
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
    @Transactional
    public List<FileUploadDTO> getAllFiles(Long notNum) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> files = fileUploadRepository.findByNotice(notice);
        List<FileUploadDTO> fileList = new ArrayList<>();
        for (FileUpload file : files) {
            FileUploadDTO fileUploadDTO = fileUploadService.entityToDTO(file);
            fileList.add(fileUploadDTO);
        }
        return fileList;
    }

    @Override
    public Long register(NoticeFormDTO noticeFormDTO, Long acaNum) {
        Notice notice = formToEntity(noticeFormDTO);
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public void registerFileAndImg(MultipartFile[] files, MultipartFile[] images, List<List> imgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                FileUpload fileUpload = fileUploadService.fileToEntity(file, member);
                fileUploadService.uploadFile(fileUpload, file);
                fileUpload.setNotice(notice);
                notice.addFile(fileUpload);
                fileUploadRepository.save(fileUpload);
            }
        }
        if (images != null && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                Image img = imageService.fileToEntity(images[i], member);
                img.setNotice(notice, (Integer) imgArray.get(i).get(1), imgArray.get(i).get(2).toString());
                notice.addImage(img);
                imageRepository.save(img);
            }
        }
        noticeRepository.save(notice);
    }

    @Override
    public void deleteFile(Long notNum, Long fileId) {
        Notice notice = noticeRepository.findByNotNum(notNum);
        FileUpload fileUpload = fileUploadRepository.findByFileId(fileId);
        notice.removeFile(fileUpload);
        fileUploadRepository.delete(fileUpload);
    }

    @Override
    public void deleteFiles(Long notNum) {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> fileList = fileUploadRepository.findByNotice(notice);
        for (FileUpload file : fileList) {
            notice.removeFile(file);
            fileUploadRepository.delete(file);
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
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> fileList = fileUploadRepository.findByNotice(notice);
        for (FileUpload file : fileList) {
            deleteFile(notNum, file.getFileId());
        }
        noticeRepository.deleteById(notNum);
    } // 수정하기

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
