package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RequestAuth;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.RequestAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RequestAuthRepository requestAuthRepository;
    private final RedisTemplate redisTemplate;


    @Override
    public void requestAuth(String email, Roles roles){
        RequestAuth requestAuth = new RequestAuth(email, roles);
        requestAuthRepository.save(requestAuth);
    }

    @Override
    public AuthDTO getOne(Member member, Roles roles) {
        RequestAuth requestAuth = requestAuthRepository.findByEmailAndRoles(member.getEmail(), roles);
        AuthDTO authDTO = entityToDTO(requestAuth, member);
        return authDTO;
    }

    @Override
    public Slice<RequestAuth> requestedAuths(Pageable pageable) {
        Slice<RequestAuth> requestAuth = requestAuthRepository.findByProcessedFalse(pageable);
        return requestAuth;
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
            redisTemplate.expire(request, 60*60*24, TimeUnit.SECONDS);
        }
    }

    @Override
    public void rejectAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        Optional<RequestAuth> requestAuth = requestAuthRepository.findById(email);
        if (requestAuth.isPresent()) {
            RequestAuth request = requestAuth.get();
            request.process(true);
            redisTemplate.expire(request, 60*60*24, TimeUnit.SECONDS);
        }
    }

    @Override
    public void deleteAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        member.removeRoles(roles);
    }

}
