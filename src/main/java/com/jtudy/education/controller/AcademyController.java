package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyMemberService;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.ImageService;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final AcademyService academyService;
    private final AcademyMemberService academyMemberService;
    private final ImageService imageService;

    private final TemplateEngine templateEngine;

    @ModelAttribute("subject")
    public Subject[] subject() {
        return Subject.values();
    }

    @GetMapping("/main")
    public void main(@AuthenticationPrincipal SecurityMember member, Model model) {
        try {
            if (member != null) {
                boolean isAdmin = member.getMember().getRolesList().contains(Roles.ADMIN);
                model.addAttribute("isAdmin", isAdmin);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/exception")
    public String exceptionalPage(Model model) {
        model.addAttribute("msg", "");
        return "/academy/exception";
    }

    @GetMapping("/list")
    public void list(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AcademyDTO> academyDTO = academyService.getAll(pageable);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/manage")
    public void manage(@RequestParam(value = "page", defaultValue = "1") int page, Model model, @AuthenticationPrincipal SecurityMember member) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AcademyDTO> academyDTO = academyService.manageAcademies(member.getMember(), pageable);
        model.addAttribute("academy", academyDTO);
        model.addAttribute("name", member.getMember().getName());
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
    public ResponseEntity register(@RequestPart @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, @RequestPart(required = false) MultipartFile file,
                                   @AuthenticationPrincipal SecurityMember member, Model model, HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.status(422).body(map);
            }
            model.addAttribute("academy", new AcademyFormDTO());
            Long number = academyService.register(academyFormDTO, member.getMember());
            model.addAttribute("number", number);
            if (file != null && !file.isEmpty()) {
                academyService.registerImg(file, number, member.getMember());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(number);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/read")
    public void academy(@RequestParam(value = "number") Long acaNum, Model model) throws FileNotFoundException {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("academy", academyDTO);
        try {
            ImageDTO fileDTO = imageService.getAcademyMain(acaNum);
            model.addAttribute("path", fileDTO.getPath());
        } catch (NullPointerException e) {
            model.addAttribute("path", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/modify")
    public String modify(@RequestParam(value = "number") Long acaNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyService.isManager(acaNum, member)) {
            model.addAttribute("academy", academyDTO);
            return "academy/modifyForm";
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "/academy/exception";
        }
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestPart @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, @RequestPart(required = false) MultipartFile file,
                                 Model model, @AuthenticationPrincipal SecurityMember member) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.status(422).body(map);
            }
            model.addAttribute("number", academyFormDTO.getAcaNum());
            model.addAttribute("academy", academyFormDTO);
            academyService.update(academyFormDTO);
            if (file != null && !file.isEmpty()) {
                academyService.changeImg(file, academyFormDTO.getAcaNum(), member.getMember());
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (academyService.isManager(acaNum, member)) {
            academyService.delete(acaNum);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "/academy/exception";
        }
        return "redirect:/academy/list";
    }

    @PostMapping("/deleteImg")
    @ResponseBody
    public String deleteImg(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (academyService.isManager(acaNum, member)) {
            academyService.removeImg(acaNum);
            return null;
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "/academy/exception";
        }
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
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        String name = member.getMember().getName();
        model.addAttribute("name", name);
        Long memNum = member.getMember().getMemNum();
        Page<AcademyDTO> academyDTO = academyService.getAcademies(memNum, pageable);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/search")
    public String search(@RequestParam(value="name", required = false) String name, @RequestParam(value="subject", required = false) String[] subject,
                         @RequestParam(value="location", required = false) String location, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
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
