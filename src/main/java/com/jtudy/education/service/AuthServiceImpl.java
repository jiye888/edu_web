package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RequestAuth;
import com.jtudy.education.repository.AuthRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.RequestAuthRepository;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;

    @Override
    public Long requestAuth(Member member, Roles roles, String content) {
        Auth auth = new Auth(member, roles, content);
        auth = authRepository.save(auth);
        return auth.getAuthId();
    }

    @Override
    public void modifyRequest(Member member, Roles roles, String content) {
        Auth auth = authRepository.findByEmail(member.getEmail());
        if (auth != null) {
            auth.changeRequest(roles, content);
            authRepository.save(auth);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO getOne(Member member) {
        Auth auth = authRepository.findByEmail(member.getEmail());
        AuthDTO authDTO = entityToDTO(auth);
        return authDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<AuthDTO> requestedAuths(Pageable pageable) {
        Slice<Auth> auth = authRepository.findByIsProcessed(false, pageable);
        Slice<AuthDTO> authDTO = auth.map(e -> entityToDTO(e));
        return authDTO;
    }

    @Override
    public void acceptAuth(String email, Roles roles){
        Member member = memberRepository.findByEmail(email);
        Auth auth = authRepository.findByEmail(email);
        if (auth != null) {
            member.addRoles(roles);
            auth.process();
        }
    }

    @Override
    public void rejectAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        Auth auth = authRepository.findByEmail(email);
        if (auth != null) {
            auth.process();
        }
    }

    @Override
    public void deleteAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        member.removeRoles(roles);
    }

}
