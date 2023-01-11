package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.AcademyFormDTO;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Member;
import com.jtudy.education.service.AcademyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final AcademyService academyService;

    @ModelAttribute("subject")
    public Subject[] subject() {
        return Subject.values();
    }

    @GetMapping("/list")
    public void list(Model model) {
        Page<AcademyDTO> academyDTO = academyService.getAll();
        model.addAttribute("academy", academyDTO);
        //model.addAttribute("list", academyService.getList(AcademyDTO, pageable));
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("academy", new AcademyFormDTO());
        return "/academy/registerForm";
        //academyForm이라는 view page 만들기. academy 등록에 필요한 양식들 존재.
    }

    @PostMapping("/register")
    public String register(@Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        System.out.println(academyFormDTO);
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
    public String modify(@RequestParam(value = "number") Long acaNum, Model model) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("academy", academyDTO);
        return "/academy/modifyForm";
    }

    @PostMapping("/modify")
    public String modify(@Valid AcademyFormDTO academyFormDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("msg", "모든 항목을 입력해주세요.");
            return "/academy/modifyForm";
        }
        academyService.update(academyFormDTO);
        return "redirect:/academy/list";
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(value = "number") Long acaNum) {
        academyService.delete(acaNum);
        return "redirect:/academy/list";
    }
    //#
    @PostMapping("/join")
    public String join(@AuthenticationPrincipal Member member) {
        //academyService에 join 생성
        return "redirect:/academy/list";
    }
}
