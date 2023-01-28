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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

@Controller
@RequestMapping("/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final AcademyService academyService;
    private final AcademyMemberService academyMemberService;

    @ModelAttribute("subject")
    public Subject[] subject() {
        return Subject.values();
    }

    @GetMapping("/main")
    public void main() {
    }

    @GetMapping("/list")
    public void list(Model model) {
        Page<AcademyDTO> academyDTO = academyService.getAll();
        model.addAttribute("academy", academyDTO);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("academy", new AcademyFormDTO());
        return "academy/registerForm";
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid Map<String, Object> form, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal Member member) {
        AcademyFormDTO academyFormDTO = new AcademyFormDTO(form);
        if (bindingResult.hasErrors()) {
            return "academy/registerForm";
        }
        Long acaNum = academyService.register(academyFormDTO);
        redirectAttributes.addFlashAttribute("message", acaNum);
        return "redirect:/academy/list";
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
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "academy/modifyForm";
    }

    @PostMapping("/modify")
    public void modify(@RequestBody Map<String, Object> form, Model model) {
        AcademyFormDTO academyFormDTO = new AcademyFormDTO(form);
        model.addAttribute("number", academyFormDTO.getAcaNum());
        model.addAttribute("academy", academyFormDTO);
        academyService.update(academyFormDTO);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyService.validateMember(acaNum, member)) {
            academyService.delete(acaNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "redirect:/academy/list";
    }
    //#
    @RequestMapping(value = "/join", method = {RequestMethod.GET, RequestMethod.POST})
    public String join(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member) {
        academyMemberService.join(member.getMember().getMemNum(), acaNum);
        return "redirect:/academy/list";
    }
    //#
    @RequestMapping(value = "/withdraw", method = {RequestMethod.GET, RequestMethod.POST})
    public String withdraw(@RequestParam(value = "number") Long acaNum, @AuthenticationPrincipal SecurityMember member) {
        academyMemberService.withdraw(member.getMember().getMemNum(), acaNum);
        return "redirect:/academy/list";
    }
    //#
    @GetMapping("/joined")
    public void getAcademies(@AuthenticationPrincipal SecurityMember member, Model model) {
        Page<AcademyDTO> academyDTO = academyService.getAcademies(member.getMember().getMemNum());
        model.addAttribute("academy", academyDTO);
    }


}
