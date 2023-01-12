package com.jtudy.education.controller;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.security.SecurityConfig;
import com.jtudy.education.service.MemberServiceImpl;
import com.jtudy.education.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String main() {
        return "/member/login";
    }

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("member", new MemberFormDTO());
        return "/member/memberForm";
    }

    @PostMapping("/join")
    public String join(Model model, @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult) {
        /*
        if (bindingResult.hasErrors()) {
            return "/member/memberForm";
        }
         */
        try {
            memberService.createMember(memberFormDTO);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/member/memberForm";
        }
        return "redirect:/academy/main";
        //return "redirect:/member/login";
    }

    @PostMapping("/withdraw")
    public String withdraw(Long memNum) {
        memberService.withdraw(memNum);
        return "redirect:/main";
    }
    @GetMapping("check")
    public String loginCheck(BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginError", "아이디 또는 비밀번호를 확인해주세요.");
            return "/member";
        }
        return "redirect:/main";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "/member/login";
    }

    @PostMapping("/login")
    public String login(String email, String password) {
        memberService.login(email, password);
        return "redirect:/main";
    }

}
