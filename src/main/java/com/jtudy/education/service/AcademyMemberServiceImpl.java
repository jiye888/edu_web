package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AcademyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AcademyMemberServiceImpl {

    private final AcademyMemberRepository academyMemberRepository;

    /*implements AcademyMemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AcademyMemberDTO> getList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "amNum"));
        Page<SubjectClass> academyMember = academyMemberRepository.findAllPaging(pageable);
        Page<AcademyMemberDTO> academyMemberDTO = academyMember.map(e -> entityToDTO(e));
        return academyMemberDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public AcademyMemberDTO get(Long amNum) {
        SubjectClass academyMember = academyMemberRepository.findByAmNum(amNum);
        AcademyMemberDTO academyMemberDTO = entityToDTO(academyMember);
        return academyMemberDTO;
    }

    @Override
    public Long register(AcademyMemberDTO academyMemberDTO) {
        SubjectClass academyMember = dtoToEntity(academyMemberDTO);
        academyMemberRepository.save(academyMember);
        return academyMember.getAmNum();
    }

    public Long addMember(AcademyMemberDTO academyMemberDTO, MemberDTO memberDTO) {
        SubjectClass academyMember = academyMemberRepository.getReferenceByAmNum(academyMemberDTO.getAmNum());
        Member member = memberRepository.findByMemNum(memberDTO.getMemNum());
        academyMember.addMember(member);
        return academyMember.getAmNum();
    }

    public Long removeMember(Long amNum, Long memNum) {
        //AcademyMember academyMember = academyMemberRepository.getReferenceByAmNum(academyMemberDTO.getAmNum());
        SubjectClass academyMember = academyMemberRepository.findByAmNum(amNum);
        //Member member = memberRepository.findByMemNum(memberDTO.getMemNum());
        Member member = memberRepository.findByMemNum(memNum);
        if (!academyMember.getMembers().isEmpty()){
            academyMember.removeMember(member);
        }
        return academyMember.getAmNum();
    }

    @Override
    public void delete(Long amNum) {
        academyMemberRepository.deleteById(amNum);
    }

 */

}
