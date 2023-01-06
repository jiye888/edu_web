package com.jtudy.education.repository;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.mysql.cj.util.StringUtils;
import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AcademyRepositoryTest {

    @Autowired
    private AcademyRepository academyRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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
                .build();
        member.addRoles(Roles.STUDENT);

        Academy academy = Academy.builder()
                .acaName("영어학원")
                .location("Seoul")
                .subject(EnumSet.of(Subject.ENGLISH))
                .build();

        AcademyMember academyMember = AcademyMember.builder()
                .academy(academy)
                .member(member)
                .build();

        //academyRepository.save(academy);
        //memberRepository.save(member);
        Academy result = academyRepository.findByAcaNum(Long.parseLong("1"));
        assertEquals(result.getAcaNum(), Long.parseLong("1"));
        Page<Academy> location = academyRepository.findByLocationContaining("Seoul", Pageable.unpaged());
        logger.info(location);


    }

    @Test
    public void gradingTest() {
/*
        Page<Object[]> academyWithGrade = academyRepository.getAcademyWithReview(Long.parseLong("4"), PageRequest.of(0, 10));

        System.out.println(academyWithGrade);

        for (Object[] a : academyWithGrade) {
            System.out.println(Arrays.toString(a));
        }
        //logger.info(academyWithGrade.getGrade());
 */
/*
        Page<Object[]> academies = academyRepository.getAcademyWithReview(PageRequest.of(0, 10));
        Object[] academyList = academies.getContent().get(0);
        Academy academyNew = (Academy) academyList[0];
        System.out.println(Arrays.toString(academyList));
        System.out.println(academyNew);

 */
    }


}