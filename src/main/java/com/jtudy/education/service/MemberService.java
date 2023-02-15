package com.jtudy.education.service;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public interface MemberService {

    boolean validateMember(Long memNum, SecurityMember securityMember);

    MemberDTO getOne(Long memNum);

    Member findOne(String email);

    Long createMember(MemberFormDTO memberFormDTO);

    Long updateMember(MemberFormDTO memberFormDTO);

    void withdraw(Long memNum);

    String login(String email, String password);

    void logout(String email);

    Page<MemberDTO> getMembers(Long acaNum, Pageable pageable);

    LocalDateTime getJoinedDate(Long acaNum, Long memNum);

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
