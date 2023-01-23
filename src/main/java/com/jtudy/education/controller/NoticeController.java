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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    }

    @GetMapping("/")
    public void read(@RequestParam("number") Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute(noticeDTO);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("noticeForm", new NoticeFormDTO());
        return "notice/noticeForm";
    }

    @PostMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, NoticeFormDTO noticeFormDTO, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal SecurityMember member) {
        //noticeDTO 객체로 들어오지 않으니 직접 Map<String, Object>로 리턴타입 설정하고
        //DTO로 가서 직접 등록
        AcademyDTO academyDTO = academyService.getOne(acaNum);
        if (academyDTO.getManagerEmail() == member.getUsername()) {
            Long notNum = noticeService.register(noticeFormDTO, acaNum);
            redirectAttributes.addFlashAttribute("message", notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "redirect:/notice";
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long notNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        NoticeFormDTO noticeFormDTO = noticeService.getForm(notNum);
        if (noticeService.validateMember(notNum, member)) {
            model.addAttribute("noticeForm", noticeFormDTO);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "notice/noticeForm";
    }

    @PostMapping("/modify")
    public String modify(NoticeFormDTO noticeFormDTO, RedirectAttributes redirectAttributes) {
        Long notNum = noticeService.update(noticeFormDTO);
        redirectAttributes.addFlashAttribute("message", notNum);
        return "redirect:/notice/list";
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam("number") Long notNum, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(notNum, member)) {
            noticeService.delete(notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
        return "redirect:/notice/list";
    }

}
