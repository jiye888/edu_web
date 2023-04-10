package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.AuthService;
import com.jtudy.education.service.MemberService;
import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AcademyService academyService;
    private final AuthService authService;

    private final TemplateEngine templateEngine;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("member", new MemberFormDTO());
        return "member/registerForm";
    }

    @PostMapping("/join")
    public ResponseEntity join(Model model, @RequestBody @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult) {
        try {
            memberService.validateEmail(memberFormDTO.getEmail());
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField() + "Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
            memberService.createMember(memberFormDTO);
        } catch (DuplicateMemberException e) {
            Map<String, String> map = new HashMap<>();
            map.put("EmailValidationError", e.getMessage());
            return ResponseEntity.badRequest().body(map);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/request")
    public ResponseEntity requestInfo(@AuthenticationPrincipal SecurityMember member, Model model) {
        try {
            Long number = member.getMember().getMemNum();
            return ResponseEntity.ok().body(number);
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/find")
    public ResponseEntity findMember(@RequestParam("email") String email, Model model) {
        try {
            MemberDTO member = memberService.findByEmail(email);
            model.addAttribute("member", member);
            return ResponseEntity.ok(member.getMemNum());
        } catch (NullPointerException e) {
            String msg = "해당 이메일을 사용중인 회원이 없습니다.";
            model.addAttribute("msg", msg);
            return ResponseEntity.badRequest().body(msg);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity member(@RequestParam(value = "number") Long memNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        try {
            if (memberService.validateMember(memNum, member)) {
                MemberDTO memberDTO = memberService.getOne(memNum);
                model.addAttribute("memberDTO", memberDTO);
                Context context = new Context();
                context.setVariables(model.asMap());
                String template = templateEngine.process("member/read", context);
                return ResponseEntity.ok().body(template);
            } else {
                String message = "권한이 없습니다.";
                model.addAttribute("msg", message);
                Context context = new Context();
                context.setVariables(model.asMap());
                String template = templateEngine.process("/academy/exception", context);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(template);
            }
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(error);
        }

    }

    @GetMapping("/modify")
    public String modify(@RequestParam(value = "number") Long memNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        if (memberService.validateMember(memNum, member)) {
            MemberDTO memberDTO = memberService.getOne(memNum);
            model.addAttribute("member", memberDTO);
        } else {
            return "academy/main";
        }
        return "member/modifyForm";
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestBody @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult,Model model) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField() + "Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
            memberService.updateMember(memberFormDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    @ResponseBody
    public void withdraw(@RequestParam("number") Long memNum) {
        memberService.withdraw(memNum);
    }

    @GetMapping("/login")
    public void login() {
    }

    @PostMapping("/loginCheck")
    public ResponseEntity loginCheck(@RequestBody Map<String, String> member) {
        try {
            String email = member.get("email");
            String password = member.get("password");
            String token = memberService.login(email, password);
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void logout(@AuthenticationPrincipal SecurityMember member) {
            memberService.logout(member.getUsername());
    }

    @GetMapping("/joined")
    public String getMembers(@RequestParam("number") Long acaNum, @RequestParam(value="page", defaultValue="1") int page, Model model, @AuthenticationPrincipal SecurityMember member){
        try {
            if (academyService.isManager(acaNum, member)) {
                Pageable pageable = PageRequest.of(page - 1, 10);
                AcademyDTO academyDTO = academyService.getOne(acaNum);
                model.addAttribute("name", academyDTO.getAcaName());
                Page<MemberDTO> memberDTO = memberService.getMembers(acaNum, pageable);
                model.addAttribute("member", memberDTO);
                return "/member/joined";
            } else {
                model.addAttribute("msg", "관리자 권한이 없습니다.");
                return "/academy/exception";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
