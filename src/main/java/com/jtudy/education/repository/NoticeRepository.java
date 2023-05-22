package com.jtudy.education.repository;

import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {

    @Query("select n from Notice n left outer join Academy a on n.academy = a where a.acaNum = :acaNum")
    Page<Notice> findByAcaNum(Pageable pageable, Long acaNum);

    Page<Notice> findAll(Specification<Notice> spec, Pageable pageable);

    Notice findByNotNum(Long notNum);

    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);

    Page<Notice> findByContentContaining(String keyword, Pageable pageable);

    Page<Notice> findByTitleOrContentContaining(String keyword1, String keyword2, Pageable pageable);

}
