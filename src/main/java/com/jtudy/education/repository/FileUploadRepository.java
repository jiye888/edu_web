package com.jtudy.education.repository;

import com.jtudy.education.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    @Query("select f from FileUpload f where f.academy.acaNum = :acaNum")
    FileUpload findByAcaNum(Long acaNum);

    @Query("select f from FileUpload f where f.notice.notNum = :notNum")
    List<FileUpload> findByNotNum(Long notNum);

    @Query("select f from FileUpload f where f.review.revNum = :revNum")
    List<FileUpload> findByRevNum(Long revNum);

}
