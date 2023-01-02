package com.jtudy.education.controller;

import com.jtudy.education.DTO.LoginFormDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.security.util.Password;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;

    @GetMapping
    public String main() {
        return "/login";
    }

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "/member/join";
    }

    @PostMapping("/join")
    public String join(Model model, @Valid MemberDTO memberDTO, BindingResult bindingResult, PasswordEncoder passwordEncoder) {
        if (bindingResult.hasErrors()) {
            return "/member/join";
        }
        try {
            memberService.createMember(memberDTO, passwordEncoder);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/member/join";
        }
        return "redirect:/member/login";
    }

    @PostMapping("/withdraw")
    public String withdraw(Long memNum) {
        memberService.withdraw(memNum);
        return "redirect:/main";
    }
/*
    @GetMapping("check")
    public String loginCheck(BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginError", "아이디 또는 비밀번호를 확인해주세요.");
            return "/member";
        }
        return "redirect:/main";
    }
 */

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("member", new MemberFormDTO());
        return "/member/login";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginFormDTO member) {
        memberService.login(member);
        return "redirect:/"
    }

}
