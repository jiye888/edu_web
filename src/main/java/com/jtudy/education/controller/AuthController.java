package com.jtudy.education.controller;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AuthService;
import com.jtudy.education.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final TemplateEngine templateEngine;

    @ModelAttribute("roles")
    public Map<Roles, String> roles() {
        Map<Roles, String> map = new HashMap<>();
        map.put(Roles.STUDENT, "학생");
        map.put(Roles.MANAGER, "학원 관리자");
        return map;
    }

    @GetMapping("/requested")
    public String requestedAuths(@RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10);
        Slice<AuthDTO> authDTO = authService.requestedAuths(pageable);
        model.addAttribute("auth", authDTO);
        return "auth/requested";
    }
    @GetMapping("/requestedAuth")
    public ResponseEntity requestedAuthData(@RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10);
        Slice<AuthDTO> authDTO = authService.requestedAuths(pageable);
        return ResponseEntity.ok().body(authDTO);
    }

    @GetMapping("/get")
    public String getRequestedOne(@RequestParam(value="number") Long authId, @AuthenticationPrincipal SecurityMember member, Model model) {
        try {
            if (member.getMember().getRolesList().contains(Roles.ADMIN)) {
                model.addAttribute("isAdmin", true);
            }
            if (authService.validateMember(authId, member)) {
                AuthDTO authDTO = authService.getOneByAuthId(authId);
                model.addAttribute("auth", authDTO);
                return "auth/get";
            } else {
                model.addAttribute("msg", "접근 권한이 없습니다."); //*exception
                return "/academy/exception";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/modify")
    public String modifyRequest(@AuthenticationPrincipal SecurityMember member, Model model) {
        try {
            AuthDTO authDTO = authService.getOne(member.getMember());
            if(authDTO != null) {
                model.addAttribute("auth", authDTO);
            }
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            return "/academy/exception";
        }
        return "auth/modifyForm";
    }

    @PostMapping("/modify")
    @ResponseBody
    public void modifyRequest(@RequestBody Map<String, String> map, @AuthenticationPrincipal SecurityMember member) {
        Roles roles = Roles.valueOf(map.get("roles"));
        String content = map.get("content").toString();
        authService.modifyRequest(member.getMember(), roles, content);
    }

    @GetMapping("/request")
    public ResponseEntity requestAuth(@AuthenticationPrincipal SecurityMember member, Model model) {
        model.addAttribute("member", memberService.getOne(member.getMember().getMemNum()));
        try {
            if (authService.getOne(member.getMember()) == null) {
                AuthDTO authDTO = new AuthDTO();
                model.addAttribute("auth", authDTO);
                Context context = new Context();
                context.setVariables(model.asMap());
                String template = templateEngine.process("auth/requestForm", context);
                return ResponseEntity.ok().body(template);
            } else {
                Long authId = authService.getOne(member.getMember()).getAuthId();
                Map<String, String> map = new HashMap<>();
                map.put("number", authId.toString());
                return ResponseEntity.ok().body(map);
            }
        }catch(Exception e) {
            model.addAttribute("msg", e.getMessage());
            Context context = new Context();
            context.setVariables(model.asMap());
            String template = templateEngine.process("/academy/exception", context);
            return ResponseEntity.badRequest().body(template);
        }
    }

    @PostMapping("/request")
    @ResponseBody
    public ResponseEntity requestAuth(@RequestBody Map<String, String> map, @AuthenticationPrincipal SecurityMember member) {
        Roles roles = Roles.valueOf(map.get("roles"));
        String content = map.get("content");
        Long authId = authService.requestAuth(member.getMember(), roles, content);
        return ResponseEntity.status(HttpStatus.OK).body(authId);
    }

    @PostMapping("/cancel")
    @ResponseBody
    public void cancelRequest(@RequestBody Map<String,Long> map, @AuthenticationPrincipal SecurityMember member) {
        Long authId = map.get("authId");
        if (authService.validateMember(authId, member)) {
            authService.cancelRequest(authId);
        }
    }

    @PostMapping("/accept")
    @ResponseBody
    public void acceptAuth(@RequestBody Map<String,Long> map, @AuthenticationPrincipal SecurityMember member) {
        Long authId = map.get("authId");
        AuthDTO authDTO = authService.getOneByAuthId(authId);
        authService.acceptAuth(authDTO.getEmail(), authDTO.getRoles());
    }

    @PostMapping("/reject")
    @ResponseBody
    public void rejectRequest(@RequestBody Map<String,Long> map, @AuthenticationPrincipal SecurityMember member) {
        Long authId = map.get("authId");
        AuthDTO authDTO = authService.getOneByAuthId(authId);
        authService.rejectAuth(authDTO.getEmail(), authDTO.getRoles());
    }

    @GetMapping("/change")
    public String changeAuth(@RequestParam Long number, Model model, @AuthenticationPrincipal SecurityMember member) {
        MemberDTO memberDTO = memberService.getOne(number);
        model.addAttribute("member", memberDTO);
        Map<Roles, String> koreanRoles = roles();
        koreanRoles.put(Roles.ADMIN, "운영자");
        model.addAttribute("koreanRoles", koreanRoles);

        Map<Roles, String> currentRoles = authService.rolesMap(memberDTO.getRolesList());
        model.addAttribute("currentRoles", currentRoles);
        return "auth/change";
    }

    @PostMapping("/remove_role")
    @ResponseBody
    public void removeRoles(@RequestBody Map<String, Object> map, @AuthenticationPrincipal SecurityMember member) {
        Long number = Long.parseLong(map.get("number").toString());
        Roles roles = Roles.valueOf(map.get("roles").toString());
        authService.removeRoles(number, roles);
    }

    @PostMapping("/add_role")
    @ResponseBody
    public void addRoles(@RequestBody Map<String, Object> map, @AuthenticationPrincipal SecurityMember member) {
        Long number  = Long.parseLong(map.get("number").toString());
        Roles roles = Roles.valueOf(map.get("roles").toString());
        authService.addRoles(number, roles);
    }
}
