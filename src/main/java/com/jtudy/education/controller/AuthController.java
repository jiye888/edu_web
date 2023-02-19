package com.jtudy.education.controller;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.RequestAuth;
import com.jtudy.education.repository.RequestAuthRepository;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AuthService;
import com.jtudy.education.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @GetMapping("/requested")
    public void requestedAuth(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<RequestAuth> requestAuth = authService.requestedAuths(pageable);
        model.addAttribute("auth", requestAuth);
    }

    @GetMapping("/request")
    public void requestAuth(@RequestParam Roles roles, @AuthenticationPrincipal SecurityMember member) {
        String email = member.getMember().getEmail();
        authService.requestAuth(email, roles);
    }

    @GetMapping("/get")
    public void getRequested(@RequestParam Roles roles, @AuthenticationPrincipal SecurityMember member) {

    }

    @PostMapping("/accept")
    public void acceptAuth(@RequestParam Roles roles, @AuthenticationPrincipal SecurityMember member) {

    }

    @PostMapping("/reject")
    public void rejectRequest() {

    }

    @PostMapping("/delete")
    public void deleteAuth() {

    }
}
