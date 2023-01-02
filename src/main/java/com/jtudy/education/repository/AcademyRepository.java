package com.jtudy.education.repository;

import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AcademyRepository extends JpaRepository<Academy, Long> {

    Academy findByAcaNum(Long acaNum);

    //@Query("select a, avg(r.grade, 0) from Review r left outer join Academy a where a = r.academy")
    //Page<Object[]> getAcademyGrade(Pageable pageable);

    Page<Academy> findByAcaNameContaining(String keyword, Pageable pageable);

    Page<Academy> findBySubjectContaining(String keyword, Pageable pageable);

    Page<Academy> findByLocationContaining(String keyword, Pageable pageable);

}
