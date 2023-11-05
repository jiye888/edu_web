package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyMemberService;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
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
    private static final Logger logger = LoggerFactory.getLogger(AcademyController.class);

    @ModelAttribute("subject")
    public Subject[] subject() {
        return Subject.values();
    }

    @GetMapping("/main")
    public void main(@AuthenticationPrincipal SecurityMember member, Model model) {
        if (member != null) {
            boolean isAdmin = member.getMember().getRolesList().contains(Roles.ADMIN);
            model.addAttribute("isAdmin", isAdmin);
        }
    }

    @GetMapping("/list")
    public void list(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AcademyDTO> academyDTO = academyService.getAll(pageable);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/manage")
    public String manage(@RequestParam(value = "page", defaultValue = "1") int page, Model model, @AuthenticationPrincipal SecurityMember member) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (member.getMember().getManagedAcademy() == null) {
            String msg = "관리중인 학원이 없습니다.";
            model.addAttribute("msg", msg);
            return "academy/exception";
        }
        Page<AcademyDTO> academyDTO = academyService.manageAcademies(member.getMember(), pageable);
        model.addAttribute("academy", academyDTO);
        try {
            model.addAttribute("name", member.getMember().getName());
            return "academy/manage";
        } catch (NullPointerException e) {
            String msg = "로그인이 필요한 서비스입니다.";
            model.addAttribute("msg", msg);
            return "academy/exception";
        }
    }

    @GetMapping("/register")
    public String register(@AuthenticationPrincipal SecurityMember member, Model model) {
        if (member != null) {
            model.addAttribute("academy", new AcademyFormDTO());
            return "academy/registerForm";
        } else {
            return "redirect:member/login";
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestPart @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, @RequestPart(required = false) MultipartFile file,
                                   @AuthenticationPrincipal SecurityMember member, Model model, HttpServletRequest request) {
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
        try {
            if (file != null && !file.isEmpty()) {
                academyService.registerImg(file, number, member.getMember());
            }
        } catch (IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            String msg = "학원의 대표 이미지 등록에 실패했습니다.";
            return ResponseEntity.internalServerError().body(msg);
        }
        return ResponseEntity.ok().body(number);
    }

    @GetMapping("/read")
    public void academy(@RequestParam(value = "number") Long acaNum, Model model) throws FileNotFoundException {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("academy", academyDTO);
        try {
            ImageDTO image = imageService.getAcademyMain(acaNum);
            if (image != null) {
                String imageSrc = "data: " + image.getMimeType() + ";base64, " + image.getBase64();
                model.addAttribute("imageSrc", imageSrc);
            }
        } catch (NullPointerException | IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            model.addAttribute("imageSrc", null);
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
            return "academy/exception";
        }
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestPart @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, @RequestPart(required = false) MultipartFile file,
                                 Model model, @AuthenticationPrincipal SecurityMember member) {
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
        try {
            if (file != null && !file.isEmpty()) {
                academyService.registerImg(file, academyFormDTO.getAcaNum(), member.getMember());
            }
        } catch (IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            String msg = "학원의 대표 이미지 변경에 실패했습니다.";
            return ResponseEntity.status(500).body(msg);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (academyService.isManager(acaNum, member)) {
            academyService.delete(acaNum);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "academy/exception";
        }
        return "redirect:academy/list";
    }

    @PostMapping("/deleteImg")
    @ResponseBody
    public String deleteImg(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (academyService.isManager(acaNum, member)) {
            academyService.removeImg(acaNum);
            return null;
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "academy/exception";
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
    public String getAcademies(@RequestParam(value = "page", defaultValue = "1") int page, @AuthenticationPrincipal SecurityMember member, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        try {
            String name = member.getMember().getName();
            model.addAttribute("name", name);
        } catch (NullPointerException e) {
            String msg = "로그인이 필요한 서비스입니다.";
            return msg;
        }
        Long memNum = member.getMember().getMemNum();
        Page<AcademyDTO> academyDTO = academyService.getAcademies(memNum, pageable);
        model.addAttribute("academy", academyDTO);
        return "academy/joined";
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
