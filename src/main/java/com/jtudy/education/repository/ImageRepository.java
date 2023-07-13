package com.jtudy.education.repository;

import com.jtudy.education.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i from Image i where i.academy.acaNum = :acaNum")
    Image findByAcaNum(Long acaNum);

    @Query("select i from Image i where i.notice.notNum = :notNum")
    List<Image> findByNotNum(Long notNum);

    @Query("select i from Image i where i.review.revNum = :revNum")
    List<Image> findByRevNum(Long revNum);

    @Query("select i from Image i where i.infoGuide.infoNum = :infoNum")
    List<Image> findByInfoNum(Long infoNum);

    Image findByImageId(Long imageId);

    List<Image> findByOriginalName(String originalName);

    @Query("select case when count(i) > 0 then true else false end from Image i where i.originalName = :originalName and i.notice.notNum = :notNum")
    boolean existsByOriginalNameAndNotNum(String originalName, Long notNum);

    @Query("select case when count(i) > 0 then true else false end from Image i where i.originalName = :originalName and i.review.revNum = :revNum")
    boolean existsByOriginalNameAndRevNum(String originalName, Long revNum);

    @Query("select case when count(i) > 0 then true else false end from Image i where i.originalName = :originalName and i.infoGuide.infoNum = :infoNum")
    boolean existsByOriginalNameAndInfoNum(String originalName, Long infoNum);

    @Query("select i from Image i where i.infoGuide.infoNum = :infoNum and i.originalName = :originalName")
    Image findByInfoNumAndOriginalName(Long infoNum, String originalName);

    @Query("select i from Image i where i.review.revNum = :revNum and i.originalName = :originalName")
    Image findByRevNumAndOriginalName(Long revNum, String originalName);

    @Query("select i from Image i where i.notice.notNum = :notNum and i.originalName = :originalName")
    Image findByNotNumAndOriginalName(Long notNum, String originalName);

}
