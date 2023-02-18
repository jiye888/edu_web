package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.ReviewRepository;
import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

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


    }

    @Test
    public void showReview() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademyDTO> listA = academyRepository.getAllAcademyWithReviewInfo(pageable);
        logger.info("*********"+listA);
        //logger.info(listA.get(1));
        //logger.info(listA.get(2).getGrade());
    }

    @Test
    public void updateMethod() {
        Academy academy = academyRepository.findByAcaNum(Long.parseLong("4"));
        System.out.println(academy.getAcaName());
        AcademyFormDTO academyFormDTO = AcademyFormDTO.builder()
                        .number(Long.parseLong("4"))
                                .acaName("인천제일!!")
                                        .subject(academy.getSubject())
                                                .location(academy.getLocation())
                .build();
        academyService.update(academyFormDTO);
        Academy academy2 = academyRepository.findByAcaNum(Long.parseLong("4"));
        System.out.println(academy2.getAcaName());
    }

    @Test
    public void searchWithSpecification() {
        Map<String, Object> search = new HashMap<>();
        search.put("name", "수학");
        //search.put("location", "강서구");
        List<Subject> sub = new ArrayList<>();
        //EnumSet<Subject> sub = EnumSet.noneOf(Subject.class);
        sub.add(Subject.MATH);
        sub.add(Subject.KOREAN);
        search.put("subject", sub);
        System.out.println("test subject type: "+sub.getClass());
        Pageable pageable = PageRequest.of(0, 10);

        Page<AcademyDTO> academy = academyService.search(search, pageable);
        System.out.println(academy.getContent());
        //System.out.println(academy.iterator().toString());

        //EnumSet<Subject> sub = EnumSet.of(Subject.MATH, Subject.KOREAN);
        //EnumSet<Subject> academy = academyRepositoryImpl.findBySubjectList(sub);
        System.out.println(academy);

    }

    @Test
    public void searchWithRepository() {
        List<String> list = new ArrayList<>();
        Set<Subject> set = new HashSet<>();
        EnumSet<Subject> enumSet = EnumSet.noneOf(Subject.class);
        list.add(Subject.MATH.toString());
        list.add(Subject.KOREAN.toString());
        set.add(Subject.MATH);
        set.add(Subject.KOREAN);
        enumSet.add(Subject.MATH);
        enumSet.add(Subject.KOREAN);

        //Page<Academy> page = academyRepository.findBySubjectContaining(list, PageRequest.of(0, 10));
        //Page<Academy> page = academyRepository.findBySubjectContaining(Subject.KOREAN.toString(), PageRequest.of(0,10));
        List<Academy> academies = academyRepository.findBySubjectContaining(list.get(1));
        System.out.println(academies);
        //System.out.println(page.getSize());
    }

    @Test
    public void listTest() {
        List<String> list = new ArrayList<>();
        list.add(Subject.MATH.toString());
        list.add(Subject.KOREAN.toString());
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(Subject.MATH);
        subjectList.add(Subject.KOREAN);
        System.out.println(list.toString());
        System.out.println(subjectList);
    }

}