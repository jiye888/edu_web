package com.jtudy.education.controller;

import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public void list(Model model) {

    }

    @GetMapping("/{notNum}")
    public void notice(Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute(noticeDTO);
    }

    @GetMapping("/modify/{notNum}")
    public String modify(@RequestParam("notNum") Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute("notice", noticeDTO);
        return "/notice/noticeForm";
    }

    @PostMapping("/modify/{notNum}")
    public String modify(NoticeDTO noticeDTO) {
        noticeService.update(noticeDTO);
        return "redirect:/notice/list";
    }

    @PostMapping("/delete")
    public String delete(Long notNum) {
        noticeService.delete(notNum);
        return "redirect:/notice/list";
    }

}
