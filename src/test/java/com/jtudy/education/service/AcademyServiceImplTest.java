package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.ReviewRepository;
import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AcademyServiceImplTest {

    @Autowired
    private AcademyService academyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AcademyRepository academyRepository;

    private final Logger logger = LogManager.getLogger(AcademyServiceImplTest.class);
/*
    @Test
    public void dummyData() {
        Member member2 = Member.builder()
                .name("Jeong")
                .address("DaeJeon")
                .email("je@gmail.com")
                .password("55555")
                .build();
        member2.addRoles(Roles.STUDENT);
        memberRepository.save(member2);

        Member member = memberRepository.findByMemNum(Long.parseLong("2"));

        Academy academy2 = Academy.builder()
                .acaName("welcome")
                .location("Goyang")
                .subject(EnumSet.of(Subject.MATH, Subject.KOREAN))
                .build();
        academyRepository.save(academy2);

        //Academy academy = academyRepository.findByAcaNum(Long.parseLong("1"));

        Review review = Review.builder()
                .academy(academy2)
                .title("review3")
                .content("review3")
                .writer(member2)
                .grade(5)
                .build();

        reviewRepository.save(review);
    }

 */
    @Test
    public void getAllTest() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));

        /*
        Page<Object[]> academyPage = academyRepository.getAcademyWithReview(pageable);
        Page<Academy> academy = academyPage.map(o -> (Academy) o[0]);

        System.out.println(academy.getContent().get(0));

        Page<AcademyDTO> academyDTO = academy.map(e -> academyService.entityToDTO(e));

        System.out.println(academyDTO.getContent().get(0));
        academyPage.stream().map(d -> d[0] = academyDTO);

        /*
        logger.info("***********"+academy.getContent().get(0));
        Object[] academy1 = academy.getContent().get(0);
        for (Object a : academy1) {
            System.out.println(StringUtils.defaultToString(a));
        }

         */

        /*
        Academy aa = academyRepository.findByAcaNum(Long.parseLong("7"));
        Double avg = academyRepository.getAcademyGrade(aa.getAcaNum());
        aa.builder().grade(avg).build();
        academyRepository.save(aa);

        logger.info(aa);
        logger.info(avg);
        logger.info("Grade " + aa.getGrade());

        Long count = academyRepository.getReviewCount(aa.getAcaNum());
        logger.info(count);

         */
        //Page<Object[]> pageObject = new Page<Object[]>;
        //Academy pageObject[0] = academyRepository.findAll();
        //이러고 dto로 변환하고 1에는 grade 2에는 count 넣고십따고ㅠㅠ



    }

    @Test
    public void showReview() {
        List<AcademyDTO> listA = academyRepository.getAllAcademyWithReviewInfo();
        logger.info("*********"+listA);
        //logger.info(listA.get(1));
        //logger.info(listA.get(2).getGrade());
    }

    @Test
    public void updateMethod() {
        Academy academy = academyRepository.findByAcaNum(Long.parseLong("4"));
        System.out.println("<before"+academy.getAcaName());
        AcademyFormDTO academyFormDTO = AcademyFormDTO.builder()
                        .acaNum(Long.parseLong("4"))
                                .acaName("인천제일!")
                                        .subject(academy.getSubject())
                                                .location(academy.getLocation())
                .build();
        academyService.update(academyFormDTO);
        Academy academy2 = academyRepository.findByAcaNum(Long.parseLong("4"));
        System.out.println("<after>"+academy2.getAcaName());
    }

}