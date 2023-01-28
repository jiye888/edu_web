package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademyServiceImpl implements AcademyService{

    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean validateMember(Long acaNum, SecurityMember member) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        return academy.getCreatedBy().equals(member.getUsername());
    }

    @Override
    public Page<AcademyDTO> getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<AcademyDTO> academy = academyRepository.getAllAcademyWithReviewInfo();
        Page<AcademyDTO> academyPage = new PageImpl<>(academy, pageable, academy.size());
        return academyPage;
    }

    @Override
    public AcademyDTO getOne(Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        AcademyDTO academyDTO = academyRepository.getOneAcademyWithReviewInfo(acaNum);
        return academyDTO;
    }

    @Override
    public Page<AcademyDTO> getAcademies(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Academy> academyList = academyMemberRepository.findByMember(member);
        List<AcademyDTO> academyDTOList = academyList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        PageImpl<AcademyDTO> page = new PageImpl<AcademyDTO>(academyDTOList, pageable, academyList.size());
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
        Academy academy = academyRepository.findByAcaNum(academyFormDTO.getAcaNum());
        academy.changeAcademy(academyFormDTO.getAcaName(), academyFormDTO.getSubject(),
                academyFormDTO.getLocation());
        return academy.getAcaNum();
    }

    @Override
    @Transactional
    public void delete(Long acaNum) {
        //Academy academy = academyRepository.findByAcaNum(acaNum);
        academyRepository.deleteById(acaNum);
    }

    @Override
    public Page<AcademyDTO> search(String category, String keyword) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        Page<Academy> academy = null;
        if (category.equals("name")) {
            academy = academyRepository.findByAcaNameContaining(keyword, pageable);
        } else if (category.equals("subject")) {
            academy = academyRepository.findBySubjectContaining(keyword, pageable);
        } else if (category.equals("location")) {
            academy = academyRepository.findByLocationContaining(keyword, pageable);
        }
        Page<AcademyDTO> academyDTO = academy.map(e -> entityToDTO(e));
        return academyDTO;
    }

}
