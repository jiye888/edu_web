package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
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
        //Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        //model.addAttribute("page", page);
        Pageable pageable = PageRequest.of(page-1, 10);
        Page<AcademyDTO> academyDTO = academyService.getAll(pageable);
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("academy", new AcademyFormDTO());
        return "academy/registerForm";
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Member member, Model model, HttpServletRequest request) {
        //AcademyFormDTO academyFormDTO = new AcademyFormDTO(form);
        model.addAttribute("academy", new AcademyFormDTO());
        if (bindingResult.hasErrors()) {
        }
        Long number = academyService.register(academyFormDTO);
        model.addAttribute("number", number);
        //URI baseUri = new URI(request.getRequestURI().toString());
        HttpHeaders headers = new HttpHeaders();
        String location = "/academy/read?number="+number;
        //headers.setLocation(URI.create(location));
        String current = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/"));
        //URI newLocation = URI.create(current+"/read?number="+number);
        String newLocation = current+"/read?number="+number;
        Context context = new Context();
        context.setVariables(model.asMap());
        //String html = templateEngine.process("/read/number?="+number, context);
        //return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).build();
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
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
        }
        return "academy/modifyForm";
    }

    @PostMapping("/modify")
    public void modify(@RequestBody @Valid /*Map<String, Object> form*/AcademyFormDTO academyFormDTO, Model model) {
        //AcademyFormDTO academyFormDTO = new AcademyFormDTO(form);
        //model.addAttribute("number", academyFormDTO.getAcaNum());
        model.addAttribute("number", academyFormDTO.getNumber());
        model.addAttribute("academy", academyFormDTO);
        academyService.update(academyFormDTO);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyService.validateMember(acaNum, member)) {
            academyService.delete(acaNum);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
        }
        return "redirect:/academy/list";
    }

    @RequestMapping(value = "/join", method = {RequestMethod.GET, RequestMethod.POST})
    public void join(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        Long memNum = member.getMember().getMemNum();
        academyMemberService.join(memNum, acaNum);
    }

    @RequestMapping(value = "/withdraw", method = {RequestMethod.GET, RequestMethod.POST})
    public void withdraw(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        Long memNum = member.getMember().getMemNum();
        academyMemberService.withdraw(memNum, acaNum);
    }

    @GetMapping("/joined")
    public void getAcademies(@RequestParam("number") Long number, @AuthenticationPrincipal SecurityMember member, Model model) {
        String name = member.getMember().getName();
        model.addAttribute("name", name);
        Long memNum = member.getMember().getMemNum();
        if (academyService.validateMember(memNum, member)){
            Page<AcademyDTO> academyDTO = academyService.getAcademies(memNum);
            model.addAttribute("academy", academyDTO);
        } else {
            model.addAttribute("msg", "권한이 없습니다.");
        }
    }

    @GetMapping("/search")
    public void search(@RequestParam String category, @RequestParam String keyword, Model model) {
        Page<AcademyDTO> academy = academyService.search(category, keyword);
        model.addAttribute("academy", academy);
    }

}
