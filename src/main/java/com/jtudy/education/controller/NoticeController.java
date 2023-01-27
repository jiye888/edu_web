package com.jtudy.education.controller;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.DTO.NoticeDTO;
import com.jtudy.education.DTO.NoticeFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyService;
import com.jtudy.education.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final AcademyService academyService;

    @GetMapping("/list")
    public void list(@RequestParam("academy") Long acaNum, Model model) {
        Page<NoticeDTO> noticeDTO = noticeService.getAll(acaNum);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", acaNum);
    }

    @GetMapping("/read")
    public String read(@RequestParam("number") Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", noticeDTO.getAcaNum());
        return "notice/read";
    }

    @GetMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, Model model) {
        model.addAttribute("academy", acaNum);
        model.addAttribute("notice", new NoticeFormDTO());
        return "notice/registerForm";
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> form, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal SecurityMember member) {
        Long acaNum = Long.valueOf(form.get("academy"));
        if (noticeService.validateMember(acaNum, member)) {
            NoticeFormDTO noticeFormDTO = new NoticeFormDTO(form);
            Long notNum = noticeService.register(noticeFormDTO, acaNum);
            redirectAttributes.addFlashAttribute("message", notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "redirect:/notice/list";
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long notNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            model.addAttribute("notice", noticeDTO);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "notice/modifyForm";
    }
    //#
    @PostMapping("/modify")
    public String modify(@RequestBody Map<String, String> form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        NoticeFormDTO noticeFormDTO = new NoticeFormDTO(form);
        Long notNum = noticeService.update(noticeFormDTO);
        redirectAttributes.addFlashAttribute("message", notNum);
        return "redirect:/notice/list";
    }
    //#
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam("number") Long notNum, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            noticeService.delete(notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "redirect:/notice/list";
    }

}
