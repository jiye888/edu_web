package com.jtudy.education.repository;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.service.AcademyService;
import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
public class SearchTest {

    @Autowired
    private AcademyService academyService;

    @Autowired
    private AcademyRepository academyRepository;

    private final Logger logger = LogManager.getLogger(SearchTest.class);

    @Test
    public void search() {
        Academy aa = academyRepository.findByAcaNum(Long.parseLong("1"));
        Pageable pageable = PageRequest.of(0, 10);
        logger.info(aa);
        //Page<AcademyDTO> academy = academyService.search("location", "S", pageable);
        //logger.info(academy);
        //assertNotEquals(academy, null);

    }
}
