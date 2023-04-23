package com.jtudy.education.repository;

import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    FileUpload findByFileId(Long fileId);

    List<FileUpload> findByNotice(Notice notice);

}
