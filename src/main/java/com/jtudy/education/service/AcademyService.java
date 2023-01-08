package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import org.springframework.data.domain.Page;

public interface AcademyService {

    Page<AcademyDTO> getAll();

    AcademyDTO getOne(Long acaNum);

    Page<AcademyDTO> getAcademies(Member member);

    Long register(AcademyFormDTO academyFormDTO);

    Long update(AcademyFormDTO academyFormDTO);

    void delete(Long acaNum);

    Page<AcademyDTO> search(String category, String keyword);

    default Academy dtoToEntity(AcademyDTO academyDTO) {
        /*
        AcademyMember academyMember = AcademyMember.builder()
                .amNum(academyDTO.getAmNum())
                .build();
         */

        Academy academy = Academy.builder()
                .acaNum(academyDTO.getAcaNum())
                .acaName(academyDTO.getAcaName())
                .subject(academyDTO.getSubject())
                .location(academyDTO.getLocation())
                .build();

        return academy;
    }

    default Academy formToEntity(AcademyFormDTO academyFormDTO) {
        Academy academy = Academy.builder()
                .acaName(academyFormDTO.getAcaName())
                .subject(academyFormDTO.getSubject())
                .location(academyFormDTO.getLocation())
                .build();

        return academy;
    }

    default AcademyDTO entityToDTO(Academy academy) {
        AcademyDTO academyDTO = AcademyDTO.builder()
                .acaNum(academy.getAcaNum())
                .acaName(academy.getAcaName())
                .subject(academy.getSubject())
                .location(academy.getLocation())
                .build();

        return academyDTO;
    }

}
