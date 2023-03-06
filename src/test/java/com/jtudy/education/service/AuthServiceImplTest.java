package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RefreshToken;
import com.jtudy.education.entity.RequestAuth;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.RefreshTokenRepository;
import com.jtudy.education.repository.RequestAuthRepository;
import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class AuthServiceImplTest {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RequestAuthRepository requestAuthRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void getOne() {
        String email = "33@gmail.com";
        Member member = memberRepository.findByEmail(email);
        AuthDTO authDTO = authService.getOne(member);
        System.out.println(authDTO);
    }

    @Test
    public void repositoryTest() {
        String email = "33@gmail.com";
        Optional<RequestAuth> requestAuth2 = requestAuthRepository.findById(email);
        Pageable pageable = PageRequest.of(0, 10);
        System.out.println(requestAuth2.isPresent());
        System.out.println(requestAuth2);
    }

}