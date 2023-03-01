package com.jtudy.education.controller;

import com.jtudy.education.DTO.AuthDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @ModelAttribute("roles")
    public Roles[] roles() {
        return Roles.values();
    }

    @GetMapping("/requested")
    public void requestedAuth(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<AuthDTO> authDTO = authService.requestedAuths(pageable);
        model.addAttribute("auth", authDTO);
    }

    @GetMapping("/get")
    public void getRequestedOne(@RequestParam String email, @AuthenticationPrincipal SecurityMember member, Model model) {
        AuthDTO authDTO = authService.getOne(member.getMember());
        model.addAttribute("auth", authDTO);
    }

    @GetMapping("/request")
    public String requestAuth(@AuthenticationPrincipal SecurityMember member, Model model) {
        AuthDTO authDTO = authService.getOne(member.getMember());
        if (authDTO == null) {
            return "academy/requestForm";
        } else {
            model.addAttribute("msg","이미 사용자 권한을 요청중입니다.");
            return "exception";
        }
    }

    @PostMapping("/request")
    @ResponseBody
    public void requestAuth(@RequestParam Roles roles, @RequestParam String content, @AuthenticationPrincipal SecurityMember member) {
        String email = member.getMember().getEmail();
        authService.requestAuth(email, roles, content);
    }

    @PostMapping("/accept")
    @ResponseBody
    public void acceptAuth(@RequestParam Roles roles, @RequestParam String email) {
        authService.acceptAuth(email, roles);
    }

    @PostMapping("/reject")
    @ResponseBody
    public void rejectRequest(@RequestParam Roles roles, @RequestParam String email) {
        authService.rejectAuth(email, roles);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void deleteAuth(@RequestParam Roles roles, @RequestParam String email) {
        authService.deleteAuth(email, roles);

    }
}
