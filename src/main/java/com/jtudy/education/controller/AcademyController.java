package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.service.AcademyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final AcademyService academyService;

    @GetMapping("/list")
    public void list(Model model) {
        academyService.getAll();
        //model.addAttribute("list", academyService.getList(AcademyDTO, pageable));
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("academy", new AcademyDTO());
        return "/academy/academyForm";
        //academyForm이라는 view page 만들기. academy 등록에 필요한 양식들 존재.
    }

    @PostMapping("/register")
    public String register(Model model, AcademyDTO academyDTO, RedirectAttributes redirectAttributes) {
        Long acaNum = academyService.register(academyDTO);
        redirectAttributes.addFlashAttribute("message", acaNum);
        return "redirect:/academy/list";
    }

    @GetMapping("/{acaNum}") //@PathVariable? @RequestParam?
    public void academy(Long acaNum, Model model) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute(academyDTO);
    }

    @GetMapping("/modify/{acaNum}")
    public String modify(@RequestParam("acaNum") Long acaNum, Model model) {
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        model.addAttribute("academy", academyDTO);
        return "/academy/academyForm";
    }

    @PostMapping("/modify/{acaNum}")
    public String modify(AcademyDTO academyDTO) {
        academyService.update(academyDTO);
        return "redirect:/academy/list";
    }

    @PostMapping("/delete")
    public String delete(Long acaNum) {
        academyService.delete(acaNum);
        return "redirect:/academy/list";
    }
}
