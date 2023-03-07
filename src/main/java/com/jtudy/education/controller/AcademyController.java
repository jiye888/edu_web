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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("academy", new AcademyFormDTO());
        Long number = academyService.register(academyFormDTO);
        model.addAttribute("number", number);
        String current = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/"));
        String newLocation = current+"/read?number="+number;
        Context context = new Context();
        context.setVariables(model.asMap());
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, newLocation).build();
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
    @ResponseBody
    public void modify(@RequestBody @Valid AcademyFormDTO academyFormDTO, Model model) {
        model.addAttribute("number", academyFormDTO.getAcaNum());
        model.addAttribute("academy", academyFormDTO);
        academyService.update(academyFormDTO);
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

    @GetMapping("/search")
    public String search(@RequestBody Map<String, Object> search, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "acaNum");
        try {
            Page<AcademyDTO> academy = academyService.search(search, pageable);
            model.addAttribute("academy", academy);
            return "academy/search";
        } catch (Exception e) {
            String msg = e.getMessage();
            model.addAttribute("msg", msg);
            return "exception";
        }
    }

}
