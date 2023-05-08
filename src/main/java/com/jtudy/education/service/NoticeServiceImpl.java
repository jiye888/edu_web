package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.DTO.ImageDTO;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ImageDTO> getAllImages(Long notNum) {
        List<Image> image = imageRepository.findByNotNum(notNum);
        List<ImageDTO> images = image.stream().map(e -> imageService.entityToDTO(e)).collect(Collectors.toList());
        return images;
    }

    @Override
    public Image setNotice(Image image, Long notNum, List<String> imgArray) {
        if (image != null && notNum != null && imgArray != null) {
            Notice notice = noticeRepository.findByNotNum(notNum);
            image.setNotice(notice, imgArray.get(1), imgArray.get(2), imgArray.get(3));
            return image;
        }
        return null; //exception
    }

    @Override
    public Long register(NoticeFormDTO noticeFormDTO, Long acaNum) {
        Notice notice = formToEntity(noticeFormDTO);
        noticeRepository.save(notice);
        return notice.getNotNum();
    }

    @Override
    public void registerFile(MultipartFile[] files, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                FileUpload fileUpload = fileUploadService.uploadFile(file, member);
                fileUpload.setNotice(notice);
                notice.addFile(fileUpload);
                fileUploadRepository.save(fileUpload);
            }
        }

        noticeRepository.save(notice);
    }

    @Override
    public void registerImg(MultipartFile[] images, List<List<String>> imgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if (images != null && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                Image img = imageService.uploadImage(images[i], member);
                img.setNotice(notice, imgArray.get(i).get(1).toString(), imgArray.get(i).get(2).toString(), imgArray.get(i).get(3).toString());
                notice.addImage(img);
                imageRepository.save(img);
            }
            noticeRepository.save(notice);
        }
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
    public void updateFile(MultipartFile[] files, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if (fileUploadRepository.findByNotice(notice) != null && !fileUploadRepository.findByNotice(notice).isEmpty()) {
            List<FileUpload> existFiles = fileUploadRepository.findByNotice(notice);
            if (fileUploadService.needsUpdate(files, existFiles)) {
                for (MultipartFile file : files) {
                    FileUpload temp = fileUploadRepository.findByOriginalName(file.getOriginalFilename());
                    if (temp == null) {
                        fileUploadService.uploadFile(file, member);
                    } else if (temp != null) {
                        existFiles.remove(temp);
                    }
                }
                for (FileUpload leftFile : existFiles) {
                    deleteFile(notNum, leftFile.getFileId());
                }
            }
        }
    }

    @Override
    public void updateImg(MultipartFile[] images, List<List<String>> imgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if(imageRepository.findByNotNum(notNum) != null && !(imageRepository.findByNotNum(notNum).isEmpty())) {
            List<Image> existImages = imageRepository.findByNotNum(notNum);
            List<String> existNames = existImages.stream().map(e -> e.getOriginalName()).collect(Collectors.toList());
            if (imageService.needsUpdateFile(images, existImages)) {
                for (MultipartFile image : images) {
                    Integer existIndex = existNames.indexOf(image.getOriginalFilename());
                    if (existIndex > -1) { // 이름이 같은 파일이 존재
                        Image existImage = existImages.get(existIndex);
                        if (!imageService.isInRangeSize(image, existImage)) { // 같은 파일x
                            Image img = imageService.uploadImage(image, member);
                            List<String> imgArr = imageService.matchArray(img, imgArray);
                            if (imgArr != null) {
                                img = setNotice(img, notNum, imgArr);
                                imageRepository.save(img);
                                notice.addImage(img);
                                noticeRepository.save(notice);
                            }
                        }
                    } else {
                        imageService.uploadImage(image, member); //이름 같은 파일x > 추가
                    }
                }
            } if (imageService.needsUpdateInfo(images, imgArray, existImages)) { // 이름, 크기 같은 파일 > 수정
                for (MultipartFile image : images) {
                    Integer existIndex = existNames.indexOf(image.getOriginalFilename());
                    if (existIndex > -1) {
                        Image existImage = existImages.get(existIndex);
                        if (imageService.isInRangeSize(image, existImage)) {
                            for (List<String> imgArr : imgArray) {
                                if (imgArr.get(0).equals(existImage)) {
                                    imageService.updateImage(existImage, imgArr);
                                }
                            }
                        }
                    }
                }
            } else {
                existImages.forEach(i -> {
                    try {
                        imageService.deleteImage(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });// 남은 existImages 삭제
            }
        } else { // 기존에 파일이 없던 경우 > 추가
            for (MultipartFile image :images) {
                Image img = imageService.uploadImage(image, member);
                List<String> imgArr = imageService.matchArray(img, imgArray);
                if (imgArr != null) {
                    img = setNotice(img, notNum, imgArr);
                    imageRepository.save(img);
                    notice.addImage(img);
                    noticeRepository.save(notice);
                }
            }
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
    public void delete(Long notNum) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> fileList = fileUploadRepository.findByNotice(notice);
        for (FileUpload file : fileList) {
            deleteFile(notNum, file.getFileId());
        }
        List<Image> imageList = imageRepository.findByNotNum(notNum);
        for (Image image : imageList) {
            notice.removeImage(image);
            imageService.deleteImage(image);
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

    @Override
    public List<byte[]> getFilesByte(Long notNum) throws IOException {
        List<FileUploadDTO> files = getAllFiles(notNum);
        List<byte[]> filesData = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            List<String> filesPath = files.stream().map(e -> e.getFilePath()).collect(Collectors.toList());
            for (String path : filesPath) {
                byte[] fileData = Files.readAllBytes(Paths.get(path));
                filesData.add(fileData);
            }
        }
        return filesData;
    }

    @Override
    public List<byte[]> getImagesByte(Long notNum) throws IOException {
        List<ImageDTO> images = getAllImages(notNum);
        List<byte[]> imagesData = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<String> imagesPath = images.stream().map(e -> e.getPath()).collect(Collectors.toList());
            for (String path : imagesPath) {
                byte[] imageData = Files.readAllBytes(Paths.get(path));
                imagesData.add(imageData);
            }
        }
        return imagesData;
    }

}
