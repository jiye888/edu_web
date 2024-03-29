package com.jtudy.education.service;

import com.jtudy.education.DTO.*;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.FileUploadRepository;
import com.jtudy.education.repository.ImageRepository;
import com.jtudy.education.repository.NoticeRepository;
import com.jtudy.education.repository.specification.NoticeSpecification;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
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

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long acaNum, SecurityMember member) {
        try {
            Academy academy = academyRepository.findByAcaNum(acaNum);
            return academy.getCreatedBy().equals(member.getUsername()) || member.getMember().getRolesList().contains(Roles.ADMIN);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> getAll(Long acaNum, Pageable pageable) {
        Page<Notice> notice = noticeRepository.findByAcaNum(pageable, acaNum);
        Page<NoticeDTO> noticeDTO = notice.map(e -> {
            try {
                return entityToDTO(e);
            } catch (Exception exc) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(exc));
                return null;
            }
        });
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
    public List<FileUploadDTO> getAllFiles(Long notNum) {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> files = fileUploadRepository.findByNotice(notice);
        List<FileUploadDTO> fileList = new ArrayList<>();
        for (FileUpload file : files) {
            try {
                FileUploadDTO fileUploadDTO = fileUploadService.entityToDTO(file);
                fileList.add(fileUploadDTO);
            } catch (IOException e) {
                fileList.add(null);
                logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            }
        }
        return fileList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getAllImages(Long notNum) {
        List<Image> image = imageRepository.findByNotNum(notNum);
        List<ImageDTO> images = image.stream().map(e -> {
            try {
                return imageService.entityToDTO(e);
            } catch (IOException ex) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(ex));
                return null;
            }
        }).collect(Collectors.toList());
        return images;
    }

    @Override
    public void checkImageName(Long notNum, Image image) {
        if (imageRepository.existsByOriginalNameAndNotNum(image.getOriginalName(), notNum)) {
            String newName = imageService.getNewName(image);
            image.changeOriginalName(newName);
        }
    }

    @Override
    public void checkFileName(Long notNum, FileUpload fileUpload) {
        if (fileUploadRepository.existsByOriginalNameAndNotNum(fileUpload.getOriginalName(), notNum)) {
            String newName = fileUploadService.getNewName(fileUpload);
            fileUpload.changeOriginalName(newName);
        }
    }

    @Override
    public Image setNotice(Image image, Long notNum, ImgArrayDTO imgArray) {
        if (image != null && notNum != null && imgArray != null) {
            Notice notice = noticeRepository.findByNotNum(notNum);
            imageService.setInfo(image, imgArray);
            image.setNotice(notice);
            checkImageName(notNum, image);
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
                checkFileName(notNum, fileUpload);
                fileUpload.setNotice(notice);
                notice.addFile(fileUpload);
                fileUploadRepository.save(fileUpload);
            }
        }
        noticeRepository.save(notice);
    }

    @Override
    public void registerImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                ImgArrayDTO imgArr = imageService.matchDTO(image.getOriginalFilename(), imgArray);
                if (imgArr != null) {
                    Image img = imageService.fileToEntity(image, member);
                    if (imageRepository.existsByOriginalNameAndNotNum(img.getOriginalName(), notNum)) {
                        img = imageService.setNewName(img, imgArr);
                    }
                    img = setNotice(img, notNum, imgArr);
                    imageService.uploadImage(image, img);
                    notice.addImage(img);
                    imageRepository.save(img);
                }
            }
            noticeRepository.save(notice);
        }
    }

    @Override
    public void registerImg(MultipartFile image, List<ImgArrayDTO> imgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        Image img = imageService.fileToEntity(image, member);
        ImgArrayDTO imgArr = imageService.matchDTO(image.getOriginalFilename(), imgArray);
        if (imageRepository.existsByOriginalNameAndNotNum(img.getOriginalName(), notNum)) {
            img = imageService.setNewName(img, imgArr);
        }
        if (imgArr != null) {
            img = setNotice(img, notNum, imgArr);
            imageService.uploadImage(image, img);
            imageRepository.save(img);
            notice.addImage(img);
            noticeRepository.save(notice);
        }
    }

    @Override
    public void deleteFile(Long notNum, Long fileId) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        FileUpload fileUpload = fileUploadRepository.findByFileId(fileId);
        notice.removeFile(fileUpload);
        fileUploadRepository.delete(fileUpload);
        if (Files.exists(Paths.get(fileUpload.getFilePath()))) {
            Files.delete(Paths.get(fileUpload.getFilePath()));
        }
    }

    @Override
    public void updateFile(MultipartFile[] files, List<String> existFiles, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<FileUpload> fileEntities = fileUploadRepository.findByNotice(notice);
        if (files != null && files.length > 0) {
            registerFile(files, notNum, member);
        }
        if (fileEntities != null && fileEntities.size() > 0) {
            if (existFiles == null || existFiles.isEmpty()) {
                for (FileUpload fileUpload : fileEntities) {
                    deleteFile(notNum, fileUpload.getFileId());
                }
            } else {
                if (fileEntities.size() > existFiles.size()) {
                    for (FileUpload fileUpload : fileEntities) {
                        if (!(existFiles.contains(fileUpload.getOriginalName()))) {
                            deleteFile(notNum, fileUpload.getFileId());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, List<ImgArrayDTO> existImgArray, Long notNum, Member member) throws IOException {
        Notice notice = noticeRepository.findByNotNum(notNum);
        List<Image> existImages = imageRepository.findByNotNum(notNum);
        List<Image> modifyList = imageService.modifyImages(existImages, existImgArray);
        List<Image> deleteList = imageService.deleteImages(existImages, existImgArray);
        if (deleteList != null && deleteList.size() > 0) {
            for (Image deleteEntity : deleteList) {
                notice.removeImage(deleteEntity);
                imageService.deleteImage(deleteEntity);
            }
            noticeRepository.save(notice);
        }
        if (images != null && images.length > 0) {
            registerImg(images, imgArray, notNum, member);
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
            try {
                notice.removeFile(file);
                deleteFile(notNum, file.getFileId());
            } catch (IOException e) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
                notice.removeFile(file);
            }
        }
        List<Image> imageList = imageRepository.findByNotNum(notNum);
        for (Image image : imageList) {
            try {
                notice.removeImage(image);
                imageService.deleteImage(image);
            } catch (IOException e) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
                notice.removeImage(image);
            }
        }
        noticeRepository.save(notice);
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
