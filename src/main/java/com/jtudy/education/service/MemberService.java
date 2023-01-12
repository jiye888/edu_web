package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface MemberService {

    public Long createMember(MemberFormDTO memberFormDTO);

    public Long updateMember(MemberFormDTO memberFormDTO);

    public void withdraw(Long memNum);

    public String login(String email, String password);

    public Page<MemberDTO> getMembers(Academy academy);

    default MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .memNum(member.getMemNum())
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .build();

        return memberDTO;
    }


}
