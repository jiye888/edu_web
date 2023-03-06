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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public void requestedAuths(/*@RequestParam(defaultValue = "1") int page*/ Pageable pageable, Model model) {
        //Pageable pageable = PageRequest.of(page-1, 10);
        Slice<AuthDTO> authDTO = authService.requestedAuths(pageable);
        model.addAttribute("auth", authDTO);
    }

    @GetMapping("/get")
    public String getRequestedOne(@RequestParam(value="number") Long authId, @AuthenticationPrincipal SecurityMember member, Model model) {
        AuthDTO authDTO = authService.getOne(member.getMember());
        if(authDTO != null) {
            if (authDTO.getEmail().equals(member.getUsername())){
            model.addAttribute("auth", authDTO);
            return "auth/get";
            }
            else {
                model.addAttribute("msg", "본인이 아닙니다.");
                return "exception";
            }
        } else {
            model.addAttribute("msg", "요청된 권한이 없습니다.");
            return "exception";
        }
    }

    @GetMapping("/modify")
    public String modifyRequest(@AuthenticationPrincipal SecurityMember member, Model model) {
        AuthDTO authDTO = authService.getOne(member.getMember());
        model.addAttribute("auth", authDTO);
        return "auth/modifyForm";
    }

    @PostMapping("/modify")
    public void modifyRequest(@RequestBody Map<String, Object> map, @AuthenticationPrincipal SecurityMember member) {
        Roles roles = (Roles) map.get("roles");
        String content = map.get("content").toString();
        authService.modifyRequest(member.getMember(), roles, content);
    }

    @GetMapping("/request")
    public String requestAuth(@AuthenticationPrincipal SecurityMember member, Model model) {
        model.addAttribute("email", member.getUsername());
        try {
            if (authService.getOne(member.getMember()) == null) {
                AuthDTO authDTO = new AuthDTO();
                model.addAttribute("auth", authDTO);
                return "auth/requestForm";
            } else {
                model.addAttribute("msg", "이미 사용자 권한을 요청중입니다.");
                return "auth/get";
                // 체크 필수
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
            return "exception";
        }
    }

    @PostMapping("/request")
    @ResponseBody
    public ResponseEntity requestAuth(@RequestBody Map<String, String> map, @AuthenticationPrincipal SecurityMember member) {
        Roles roles = Roles.valueOf(map.get("roles"));
        String content = map.get("content");
        System.out.println("Request Auth"+roles);
        System.out.println("Request Auth"+content);
        Long authId = authService.requestAuth(member.getMember(), roles, content);
        return ResponseEntity.status(HttpStatus.OK).body(authId);
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
