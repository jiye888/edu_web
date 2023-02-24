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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void list(@RequestParam("academy") Long acaNum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "notNum");
        Page<NoticeDTO> noticeDTO = noticeService.getAll(acaNum, pageable);
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
    @ResponseBody
    public void register(@RequestBody @Valid NoticeFormDTO noticeFormDTO, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal SecurityMember member) {
        Long acaNum = noticeFormDTO.getAcademy();
        if (noticeService.validateMember(acaNum, member)) {
            Long notNum = noticeService.register(noticeFormDTO, acaNum);
            redirectAttributes.addFlashAttribute("message", notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
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

    @PostMapping("/modify")
    @ResponseBody
    public String modify(@RequestBody @Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Long notNum = noticeService.update(noticeFormDTO);
        redirectAttributes.addFlashAttribute("message", notNum);
        return "redirect:/notice/list";
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void delete(@RequestParam("number") Long notNum, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            noticeService.delete(notNum);
        } else {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
    }

    @GetMapping("/search")
    public void search(@RequestBody Map<String, String> map, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "notNum");
        Page<NoticeDTO> notice = noticeService.search(map, pageable);
        model.addAttribute("notice", notice);
    }

}
