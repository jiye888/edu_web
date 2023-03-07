package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AuthRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.RefreshTokenRepository;
import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class AuthServiceImplTest {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthRepository authRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void getOne() {
        String email = "33@gmail.com";
        Member member = memberRepository.findByEmail(email);
        AuthDTO authDTO = authService.getOne(member);
        System.out.println(authDTO);
    }


}