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
import org.springframework.web.bind.annotation.*;

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
        return "/member/registerForm";
    }

    @PostMapping("/join")
    public String join(Model model, @ModelAttribute("member") @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/member/registerForm";
        }
        try {
            memberService.createMember(memberFormDTO);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/member/registerForm";
        }
        return "redirect:/member/login";
    }

    @GetMapping("/read")
    public void member(@RequestParam(value = "id") Long memNum, Model model) {
        MemberDTO memberDTO = memberService.getOne(memNum);
        model.addAttribute("member", memberDTO);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam(value = "id") Long memNum, Model model) {
        MemberDTO memberDTO = memberService.getOne(memNum);
        model.addAttribute("member", memberDTO);
        return "/member/modifyForm";
    }

    @PostMapping("/modify")
    public String modify(@Valid MemberFormDTO memberFormDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", "모든 항목을 입력해주세요.");
            return "/member/modifyForm";
        }
        memberService.updateMember(memberFormDTO);
        return "redirect:/academy/main";
    }

    @PostMapping("/withdraw")
    public String withdraw(Long memNum) {
        memberService.withdraw(memNum);
        return "redirect:/academy/main";
    }

    @GetMapping("/check")
    public String loginCheck(BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginError", "아이디 또는 비밀번호를 확인해주세요.");
            return "/member";
        }
        return "redirect:/main";
    }

    @GetMapping("/login")
    public void login() {
    }

    @PostMapping("/loginCheck")
    public String loginCheck(String email, String password, Model model) {
        try {
            memberService.login(email, password);
        } catch (Exception e) {
            model.addAttribute("msg", "아이디와 패스워드가 일치하지 않습니다.");
            return "/member/login";
        }
        return "redirect:/academy/main";
    }

}
