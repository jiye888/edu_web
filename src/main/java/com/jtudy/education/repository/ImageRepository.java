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

}
