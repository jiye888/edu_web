package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    public Page<AcademyDTO> getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<AcademyDTO> academy = academyRepository.getAcademyWithReview();
        Page<AcademyDTO> academyPage = new PageImpl<>(academy, pageable, academy.size());
        return academyPage;
    }

    @Override
    public AcademyDTO getOne(Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        AcademyDTO academyDTO = entityToDTO(academy);
        return academyDTO;
    }

    @Override
    public Page<AcademyDTO> getAcademies(Member member) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Academy> academyList = academyMemberRepository.findByMember(member);
        academyList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        PageImpl page = new PageImpl(academyList, pageable, academyList.size());
        return page;
    }

    @Override
    public Long register(AcademyDTO academyDTO) {
        Academy academy = dtoToEntity(academyDTO);
        academyRepository.save(academy);
        return academy.getAcaNum();
    }

    @Override
    public Long update(AcademyDTO academyDTO) {
        Academy academy = academyRepository.findByAcaNum(academyDTO.getAcaNum());
        academy.changeAcademy(academyDTO.getAcaName(), academyDTO.getSubject(),
                academyDTO.getLocation());
        academyRepository.save(academy);
        return academy.getAcaNum();
    }

    @Override
    @Transactional
    public void delete(Long acaNum) {
        academyRepository.deleteById(acaNum);
    }

    @Override
    public Page<AcademyDTO> search(String category, String keyword) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        Page<Academy> academy = null;
        if (category == "name") {
            academy = academyRepository.findByAcaNameContaining(keyword, pageable);
        } else if (category == "subject") {
            academy = academyRepository.findBySubjectContaining(keyword, pageable);
        } else if (category == "location") {
            academy = academyRepository.findByLocationContaining(keyword, pageable);
        }
        Page<AcademyDTO> academyDTO = academy.map(e -> entityToDTO(e));
        return academyDTO;
    }
}
