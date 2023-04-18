package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import com.jtudy.education.repository.specification.AcademySpecification;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AcademyServiceImpl implements AcademyService{

    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional(readOnly = true)
    public boolean isManager(Long acaNum, SecurityMember securityMember) {
        try {
            Member member = securityMember.getMember();
            Academy academy = academyRepository.findByAcaNum(acaNum);
            boolean isManager = academy.getManager().getMemNum().equals(member.getMemNum());
            boolean isAdmin = member.getRolesList().contains(Roles.ADMIN);
            return (isManager || isAdmin);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        List<AcademyDTO> academyDTOList = academyList.stream().map(e -> academyRepository.getOneAcademyWithReviewInfo(e.getAcaNum())).collect(Collectors.toList());
        Page<AcademyDTO> page = new PageImpl<>(academyDTOList, pageable, academyList.size());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademyDTO> manageAcademies(Member member, Pageable pageable) {
        Page<AcademyDTO> academy = academyRepository.findByMember(member, pageable);
        return academy;
    }

    @Override
    public Long register(AcademyFormDTO academyFormDTO, Member member) {
        Academy academy = formToEntity(academyFormDTO, member);
        academy = academyRepository.save(academy);
        return academy.getAcaNum();
    }

    @Override
    public void registerImg(MultipartFile file, Long acaNum, Member member) throws IOException {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        FileUpload fileUpload = fileUploadService.fileToEntity(file, member);
        fileUpload.setAcademy(academy);
        academy.setFile(fileUpload);
        fileUploadService.uploadFile(fileUpload);
        academyRepository.save(academy);
    }

    @Override
    public Long update(AcademyFormDTO academyFormDTO) {
        Academy academy = academyRepository.findByAcaNum(academyFormDTO.getAcaNum());
        academy.changeAcademy(academyFormDTO.getAcaName(), academyFormDTO.getSubject(),
                academyFormDTO.getLocation());
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
            if(category.contains("name")) {
                spec = spec.and(AcademySpecification.nameContaining(search.get("name").toString()));
            }
            if(category.contains("location")) {
                spec = spec.and(AcademySpecification.locationContaining(search.get("location").toString()));
            }
            if(category.contains("subject")) {
                List<Subject> subjectList = (List<Subject>) search.get("subject");
                EnumSet enumSet = EnumSet.noneOf(Subject.class);
                for (Object s : subjectList) {
                    Subject sub = (Subject) s;
                    enumSet.add(sub);
                }
                enumSet.stream().map(e-> e.getClass()).forEach(System.out::println);
                if(subjectList != null && !subjectList.isEmpty()) {
                    spec = spec.and(AcademySpecification.subjectAllContaining(subjectList));
                }
            }
        }
        Page<Academy> academy = academyRepository.findAll(spec, pageable);
        Page<AcademyDTO> academyDTO = academy.map(e -> academyRepository.getOneAcademyWithReviewInfo(e.getAcaNum()));
        return academyDTO;
    }

}
