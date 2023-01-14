package com.jtudy.education.service;

import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public SecurityMember loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("<" + email + "> 사용자를 찾을 수 없습니다.");
        }
        SecurityMember securityMember = new SecurityMember(member);
        System.out.println(securityMember);
        return securityMember;
    }


}
