package com.jtudy.education.repository;

import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AcademyRepositoryTest {

    @Autowired
    private AcademyRepository academyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final Logger logger = LogManager.getLogger(AcademyRepositoryTest.class);

    @Test
    public void findTest() {
        Member member = Member.builder()
                .name("kim")
                .address("Seoul")
                .password("12345678")
                .email("123@gmail.com")
                .academyMember(null)
                .build();

        Academy academy = Academy.builder()
                .acaName("영어학원")
                .location("Seoul")
                .subject(EnumSet.of(Subject.ENGLISH))
                .build();

        Academy result = academyRepository.findByAcaNum(Long.parseLong("1"));
        assertEquals(result.getAcaNum(), Long.parseLong("1"));
        Page<Academy> location = academyRepository.findByLocationContaining("Seoul", Pageable.unpaged());
        logger.info(location);

    }

}