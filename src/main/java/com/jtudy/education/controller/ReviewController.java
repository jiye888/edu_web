package com.jtudy.education.controller;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public void list(@RequestParam("academy") Long acaNum, Model model) {
        Page<ReviewDTO> reviewDTO = reviewService.getAll(acaNum);
        model.addAttribute("academy", acaNum);
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, Model model) {
        model.addAttribute("review", new ReviewFormDTO());
        model.addAttribute("academy", acaNum);
        return "/review/registerForm";
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, Object> form, @AuthenticationPrincipal SecurityMember member, Model model) {
        ReviewFormDTO reviewFormDTO = new ReviewFormDTO(form, member.getMember().getMemNum());
        System.out.println(form.get("grade").getClass()); // int > long cast할 수 없다고 오류
        System.out.println(reviewFormDTO);
        reviewService.register(reviewFormDTO);
        return "redirect:/review/list";
    }

    @GetMapping("/read")
    public void read(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("review", reviewDTO);
        return "/review/modifyForm";
    }

    @PostMapping("/modify")
    public String modify(@Valid ReviewFormDTO reviewFormDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", "모든 항목을 입력해주세요.");
            return "/review/modifyForm";
        }
        reviewService.update(reviewFormDTO);
        return "redirect:/review/list";
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam("number") Long revNum) {
        reviewService.delete(revNum);
        return "redirect:/review/list";
    }

}
