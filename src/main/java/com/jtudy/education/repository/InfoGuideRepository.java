package com.jtudy.education.repository;

import com.jtudy.education.entity.InfoGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoGuideRepository extends JpaRepository<InfoGuide, Long>, JpaSpecificationExecutor<InfoGuide> {

    Page<InfoGuide> findAll(Pageable pageable);
    Page<InfoGuide> findAll(Specification<InfoGuide> spec, Pageable pageable);
    InfoGuide findByInfoNum(Long infoNum);

}
