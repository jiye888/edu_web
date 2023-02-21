package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import com.jtudy.education.repository.specification.AcademySpecs;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.EnumType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
@RequiredArgsConstructor
public class AcademyServiceImpl implements AcademyService{

    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long acaNum, SecurityMember member) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        return academy.getCreatedBy().equals(member.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademyDTO> getAll(Pageable pageable) {
        Page<AcademyDTO> academy = academyRepository.getAllAcademyWithReviewInfo(pageable);
        return academy;
    }

    @Override
    @Transactional(readOnly = true)
    public AcademyDTO getOne(Long acaNum) {
        AcademyDTO academyDTO = academyRepository.getOneAcademyWithReviewInfo(acaNum);
        return academyDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademyDTO> getAcademies(Long memNum, Pageable pageable) {
        Member member = memberRepository.findByMemNum(memNum);
        List<AcademyMember> academyMemberList = academyMemberRepository.findByMember(member);
        List<Academy> academyList = academyMemberList.stream().map(e -> e.getAcademy()).collect(Collectors.toList());
        List<AcademyDTO> academyDTOList = academyList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        Page<AcademyDTO> page = new PageImpl<>(academyDTOList, pageable, academyList.size());
        return page;
    }

    @Override
    public Long register(AcademyFormDTO academyFormDTO) {
        Academy academy = formToEntity(academyFormDTO);
        academy = academyRepository.save(academy);
        return academy.getAcaNum();
    }

    @Override
    public Long update(AcademyFormDTO academyFormDTO) {
        System.out.println("Number: "+academyFormDTO.getAcaNum());
        Academy academy = academyRepository.findByAcaNum(academyFormDTO.getAcaNum());
        System.out.println("Academy: "+academy);
        academy.changeAcademy(academyFormDTO.getAcaName(), academyFormDTO.getSubject(),
                academyFormDTO.getLocation());
        System.out.println(academyFormDTO);
        academyRepository.save(academy);
        return academy.getAcaNum();
    }

    @Override
    public void delete(Long acaNum) {
        academyRepository.deleteById(acaNum);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademyDTO> search(Map<String, Object> search, Pageable pageable) {
        Iterator<Map.Entry<String, Object>> iterator = search.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
            }
        }

        List<String> categories = new ArrayList<>(search.keySet());
        if (categories.isEmpty()) {
            throw new NullPointerException("검색어를 입력해주세요.");
        }

        Specification<Academy> spec = Specification.where(null);
        for (String category : categories) {
            if(category.equals("name")) {
                if(search.get("name") != null) {
                    spec = spec.and(AcademySpecs.nameContaining(search.get("name").toString()));
                }
            } else if(category.equals("location")) {
                if(search.get("location") != null) {
                    spec = spec.and(AcademySpecs.locationContaining(search.get("location").toString()));
                }
            } else if(category.equals("subject")) {
                List<Subject> subjectList = (List<Subject>) search.get("subject");
                List<String> stringList = (List<String>) search.get("subject");
                System.out.println("subjectList type: "+subjectList.getClass());
                EnumSet enumSet = EnumSet.noneOf(Subject.class);
                for (Object s : subjectList) {
                    Subject sub = (Subject) s;
                    enumSet.add(sub);
                }
                System.out.println("enumSet: "+enumSet.getClass());
                enumSet.stream().map(e-> e.getClass()).forEach(System.out::println);
                if(subjectList != null && !subjectList.isEmpty()) {
                    //EnumSet.of((subjectList.stream().map(s -> EnumSet.add("Subject."+s)));
                    //spec = spec.and(AcademySpecs.subjectAllContaining(EnumSet.copyOf(subjectList)));
                    spec = spec.and(AcademySpecs.subjectAllContaining(subjectList));
                    System.out.println("copy of: "+EnumSet.copyOf(subjectList).getClass());
                    //List<Academy> academy = academyRepository.findBySubjectList(subjectList);
                    //System.out.println("size of the result: " + academyRepository.findAll(spec).size());
                }
            }
        }
        System.out.println("spec: "+spec);
        List<Academy> academyList = academyRepository.findAll(spec);
        System.out.println("findAll(spec): "+academyList);
        Page<Academy> academy = new PageImpl<>(academyList, pageable, academyList.size());

        Page<AcademyDTO> academyDTO = academy.map(e -> entityToDTO(e));
        return academyDTO;
    }

}
