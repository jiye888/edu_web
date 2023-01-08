package com.jtudy.education.controller;

import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public void list(Model model) {

    }

    @GetMapping("/{notNum}")
    public void notice(@PathVariable Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute(noticeDTO);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("noticeForm", new NoticeFormDTO());
        return "/notice/noticeForm";
    }

    @PostMapping("/register")
    public String register(@RequestParam Long acaNum, NoticeFormDTO noticeFormDTO, RedirectAttributes redirectAttributes) {
        Long notNum = noticeService.register(noticeFormDTO, acaNum);
        redirectAttributes.addFlashAttribute("message", notNum);
        return "redirect:/notice";
    }

    @GetMapping("/modify/{notNum}")
    public String modify(@RequestParam("notNum") Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute("noticeForm", noticeDTO);
        return "/notice/noticeForm";
    }

    @PostMapping("/modify/{notNum}")
    public String modify(NoticeFormDTO noticeFormDTO, RedirectAttributes redirectAttributes) {
        Long notNum = noticeService.update(noticeFormDTO);
        redirectAttributes.addFlashAttribute("message", notNum);
        return "redirect:/notice/list";
    }

    @PostMapping("/delete")
    public String delete(Long notNum) {
        noticeService.delete(notNum);
        return "redirect:/notice/list";
    }

}
