package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.AuthService;
import com.jtudy.education.service.MemberService;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
/*
    @PostMapping("/join")
    public ResponseEntity join(Model model, @RequestBody @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //model.addAttribute("member", new MemberFormDTO());
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            Map<String, String> map = new HashMap();
            map.put("BindingResultError", "true");
            //model.addAttribute("org.springframework.validation.BindingResult.registerForm", bindingResult);
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                message.append(fieldError.getDefaultMessage());
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
                System.out.println(fieldError.getField()+"Error");
                System.out.println(fieldError);
            }
            Context context = new Context();
            context.setVariables(model.asMap());
            String template = templateEngine.process("member/registerForm", context);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        try {
            memberService.createMember(memberFormDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }*/


    @PostMapping("/join")
    public ResponseEntity join(Model model, @RequestBody @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult) {
        try {
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap();
            map.put("BindingResultError", "true");
            //model.addAttribute("org.springframework.validation.BindingResult.registerForm", bindingResult);
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                System.out.println(fieldError.getField()+"Error");
                System.out.println(fieldError);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
            memberService.createMember(memberFormDTO);
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NullPointerException e) {
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
    @ResponseBody
    public String modify(@RequestBody @Valid MemberFormDTO memberFormDTO, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()){
            return "member/modifyForm";
        }
        memberService.updateMember(memberFormDTO);
        return "redirect:/academy/main";
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
    public ResponseEntity<Map<String, String>> loginCheck(@RequestBody Map<String, String> member, Model model, HttpSession session, HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String email = member.get("email");
            String password = member.get("password");
            String token = memberService.login(email, password);
            map.put("token", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void logout(@AuthenticationPrincipal SecurityMember member) {
            memberService.logout(member.getUsername());
    }

    @GetMapping("/joined")
    public void getMembers(@RequestParam("number") Long acaNum, @RequestParam(value="page", defaultValue="1") int page, Model model){
        Pageable pageable = PageRequest.of(page-1, 10);
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("name", academyDTO.getAcaName());
        Page<MemberDTO> member = memberService.getMembers(acaNum, pageable);
        model.addAttribute("member", member);
        //Page<Object> pageObject = member.map(e -> memberService.getJoinedDate(acaNum, e.getMemNum()));
        //model.addAttribute("object", pageObject);
        //return pageObject;
    }

}
