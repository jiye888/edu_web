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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public String list(@RequestParam("academy") Long acaNum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10);
        Page<NoticeDTO> noticeDTO = noticeService.getAll(acaNum, pageable);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", acaNum);
        return "notice/list";
    }

    @GetMapping("/read")
    public String read(@RequestParam("number") Long notNum, Model model) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", noticeDTO.getAcaNum());
        return "notice/read";
    }

    @GetMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (!noticeService.validateMember(acaNum, member)) {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "exception";
        } else {
            model.addAttribute("academy", acaNum);
            model.addAttribute("notice", new NoticeFormDTO());
            return "notice/registerForm";
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, @AuthenticationPrincipal SecurityMember member) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(map);
            }
            Long acaNum = noticeFormDTO.getAcademy();
            Long notNum = noticeService.register(noticeFormDTO, acaNum);
            redirectAttributes.addFlashAttribute("message", notNum);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity modify(@RequestBody @Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> map = new HashMap<>();
                map.put("BindingResultError", "true");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(map);
            }
            Long notNum = noticeService.update(noticeFormDTO);
            redirectAttributes.addFlashAttribute("message", notNum);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@RequestParam("number") Long notNum, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            noticeService.delete(notNum);
        } else {
            //throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET/*, RequestMethod.POST*/})
    public String search(@RequestParam(value="academy") Long acaNum, @RequestParam(required = false) String title, @RequestParam(required = false) String content, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "notNum");
        try {
            Map<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("content", content);
            Page<NoticeDTO> notice = noticeService.search(acaNum, map, pageable);
            model.addAttribute("notice", notice);
            model.addAttribute("academy", acaNum);
            return "notice/search";
        } catch (Exception e) {
            String msg = e.getMessage();
            model.addAttribute("msg", msg);
            return "exception";
        }
    }

}
