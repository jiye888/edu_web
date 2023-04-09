package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface AcademyService {

    boolean isManager(Long acaNum, SecurityMember member);

    Page<AcademyDTO> getAll(Pageable pageable);

    AcademyDTO getOne(Long acaNum);

    Long register(AcademyFormDTO academyFormDTO, Member member);

    Long update(AcademyFormDTO academyFormDTO);

    void delete(Long acaNum);

    Page<AcademyDTO> getAcademies(Long memNum, Pageable pageable);

    Page<AcademyDTO> search(Map<String, Object> search, Pageable pageable);

    default Academy formToEntity(AcademyFormDTO academyFormDTO, Member member) {
        Academy academy = Academy.builder()
                .acaNum(academyFormDTO.getAcaNum())
                .acaName(academyFormDTO.getAcaName())
                .manager(member)
                .subject(academyFormDTO.getSubject())
                .location(academyFormDTO.getLocation())
                .build();

        return academy;
    }

    default AcademyDTO entityToDTO(Academy academy) {
        AcademyDTO academyDTO = AcademyDTO.builder()
                .acaNum(academy.getAcaNum())
                .acaName(academy.getAcaName())
                .managerName(academy.getManager().getName())
                .managerEmail(academy.getManager().getEmail())
                .subject(academy.getSubject())
                .location(academy.getLocation())
                .managerEmail(academy.getCreatedBy())
                .build();

        return academyDTO;
    }

}
