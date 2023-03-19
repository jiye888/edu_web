package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyMemberService;
import com.jtudy.education.service.AcademyService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@Controller
@RequestMapping("/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final AcademyService academyService;
    private final AcademyMemberService academyMemberService;

    private final TemplateEngine templateEngine;

    @ModelAttribute("subject")
    public Subject[] subject() {
        return Subject.values();
    }

    @GetMapping("/main")
    public void main() {
    }

    @GetMapping("/list")
    public void list(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        Page<AcademyDTO> academyDTO = academyService.getAll(pageable);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/register")
    public String register(@AuthenticationPrincipal SecurityMember member, Model model) {
        if (member != null) {
            model.addAttribute("academy", new AcademyFormDTO());
            return "academy/registerForm";
        } else {
            return "redirect:/member/login";
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, @AuthenticationPrincipal Member member, Model model, HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                System.out.println(map);
                return ResponseEntity.badRequest().body(map);
            }
            model.addAttribute("academy", new AcademyFormDTO());
            Long number = academyService.register(academyFormDTO);
            model.addAttribute("number", number);
            return ResponseEntity.status(HttpStatus.OK).body(number);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/read")
    public void academy(@RequestParam(value = "number") Long acaNum, Model model) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam(value = "number") Long acaNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyService.validateMember(acaNum, member)) {
            model.addAttribute("academy", academyDTO);
            return "academy/modifyForm";
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "exception";
        }
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestBody @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(map);
            }
            model.addAttribute("number", academyFormDTO.getAcaNum());
            model.addAttribute("academy", academyFormDTO);
            academyService.update(academyFormDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String delete(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        //AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyService.validateMember(acaNum, member)) {
            academyService.delete(acaNum);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "exception";
        }
        return "redirect:/academy/list";
    }

    @RequestMapping(value = "/join", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void join(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        Long memNum = member.getMember().getMemNum();
        academyMemberService.join(memNum, acaNum);
    }

    @RequestMapping(value = "/withdraw", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void withdraw(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        Long memNum = member.getMember().getMemNum();
        academyMemberService.withdraw(memNum, acaNum);
    }

    @GetMapping("/joined")
    public void getAcademies(@RequestParam(value = "page", defaultValue = "1") int page, @AuthenticationPrincipal SecurityMember member, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "acaNum");
        String name = member.getMember().getName();
        model.addAttribute("name", name);
        Long memNum = member.getMember().getMemNum();
        Page<AcademyDTO> academyDTO = academyService.getAcademies(memNum, pageable);
        model.addAttribute("academy", academyDTO);
    }
/*
    @RequestMapping(value = "/search", method = {RequestMethod.POST})
    public ResponseEntity search(@RequestBody Map<String, Object> search, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "acaNum");
        try {
            Page<AcademyDTO> academy = academyService.search(search, pageable);
            model.addAttribute("academy", academy);
            Context context = new Context();
            context.setVariables(model.asMap());
            String template = templateEngine.process("academy/search", context);
            return ResponseEntity.ok().body(template);
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            Context context = new Context();
            context.setVariables(model.asMap());
            String template = templateEngine.process("exception", context);
            return ResponseEntity.ok().body(template);
        }
    }*/

    @GetMapping("/search")
    public String search(@RequestParam(value="name", required = false) String name, @RequestParam(value="subject", required = false) String[] subject,
                         @RequestParam(value="location", required = false) String location, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10);
        List<Subject> subjectList = new ArrayList<>();
        if (subject != null) {
            for (String sub : subject) {
                subjectList.add(Subject.valueOf(sub));
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("subject", subjectList);
        map.put("location", location);
        Page<AcademyDTO> academy = academyService.search(map, pageable);
        model.addAttribute("academy", academy);
        return "academy/search";
    }

}
