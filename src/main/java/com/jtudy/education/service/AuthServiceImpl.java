package com.jtudy.education.service;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RequestAuth;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.RequestAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RequestAuthRepository requestAuthRepository;


    @Override
    public void requestAuth(String email, Roles roles){
        RequestAuth requestAuth = new RequestAuth(email, roles);
        requestAuthRepository.save(requestAuth);
    }

    @Override
    public void acceptAuth(String email, Roles roles){
        Member member = memberRepository.findByEmail(email);
        Optional<RequestAuth> requestAuth = requestAuthRepository.findById(email);
        if (requestAuth.isPresent()) {
            RequestAuth request = requestAuth.get();
            if (request.getRoles() == roles && !request.isProcessed()) {
                request.acceptAuth(email, roles);
                requestAuthRepository.save(request);
                member.getRolesList().add(roles);
            }
        }
    }

    @Override
    public void rejectAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        Optional<RequestAuth> requestAuth = requestAuthRepository.findById(email);
        if (requestAuth.isPresent()) {
            RequestAuth request = requestAuth.get();
            request.process(true);
        }
    }

    @Override
    public void deleteAuth(String email, Roles roles) {

    }

}
