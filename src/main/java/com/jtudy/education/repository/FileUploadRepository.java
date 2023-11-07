package com.jtudy.education.repository;

import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    FileUpload findByFileId(Long fileId);

    List<FileUpload> findByNotice(Notice notice);

    FileUpload findByOriginalName(String originalName);

    @Query("select case when count(f) > 0 then true else false end from FileUpload f where f.originalName = :originalName and f.notice.notNum = :notNum")
    boolean existsByOriginalNameAndNotNum(String originalName, Long notNum);

}
