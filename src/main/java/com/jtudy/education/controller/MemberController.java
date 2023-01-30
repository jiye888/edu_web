package com.jtudy.education.controller;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityConfig;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.MemberService;
import com.jtudy.education.service.MemberServiceImpl;
import com.jtudy.education.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("member", new MemberFormDTO());
        return "member/registerForm";
    }

    @PostMapping("/join")
    public String join(Model model, @ModelAttribute("member") @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/registerForm";
        }
        try {
            memberService.createMember(memberFormDTO);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "member/registerForm";
        }
        return "redirect:/member/login";
    }

    @GetMapping("/request")
    public ResponseEntity requestInfo(@AuthenticationPrincipal SecurityMember member) {
        Long number = member.getMember().getMemNum();
        return ResponseEntity.ok().body(number);
    }

    @GetMapping("/read")
    public void member(@RequestParam(value = "number") Long memNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        if (memberService.validateMember(memNum, member)) {
            MemberDTO memberDTO = memberService.getOne(memNum);
            model.addAttribute("member", memberDTO);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    @GetMapping("/modify")
    public String modify(@RequestParam(value = "number") Long memNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        if (memberService.validateMember(memNum, member)) {
            MemberDTO memberDTO = memberService.getOne(memNum);
            model.addAttribute("member", memberDTO);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        return "member/modifyForm";
    }

    @PostMapping("/modify")
    public String modify(@RequestBody Map<String, String> form, Model model) {
        System.out.println(form);
        MemberFormDTO memberFormDTO = new MemberFormDTO(form);
        System.out.println(memberFormDTO);
        memberService.updateMember(memberFormDTO);
        return "redirect:/academy/main";
    }

    @PostMapping("/withdraw")
    public String withdraw(Long memNum) {
        memberService.withdraw(memNum);
        return "redirect:/academy/main";
    }

    @GetMapping("/login")
    public void login() {
    }

    @PostMapping("/loginCheck")
    public ResponseEntity<Map<String, String>> loginCheck(@RequestBody Map<String, String> member, Model model, HttpSession session, HttpServletRequest request) {
        try {
            String email = member.get("email");
            String password = member.get("password");
            String token = memberService.login(email, password);
            HashMap<String, String> map = new HashMap<>();
            map.put("token", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            model.addAttribute("msg", "아이디와 패스워드가 일치하지 않습니다.");
            HashMap<String, String> map = new HashMap<>();
            map.put("redirect","redirect:/member/login");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout() {
        return "redirect:/academy/main";
    }

    @GetMapping("/joined")
    public void getMembers(@RequestParam("id") Long acaNum){
        memberService.getMembers(acaNum);

    }
}
