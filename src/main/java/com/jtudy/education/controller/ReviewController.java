package com.jtudy.education.controller;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public void list(Model model) {
        Page<ReviewDTO> reviewDTO = reviewService.getAll();
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("review", new ReviewFormDTO());
        return "/review/registerForm";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("review") @Valid ReviewFormDTO reviewFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "/review/registerForm";
        }
        Long revNum = reviewService.register(reviewFormDTO);
        redirectAttributes.addFlashAttribute("message", revNum);
        return "redirect:/review/list";
    }

    @GetMapping("/read")
    public void read(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("review", reviewDTO);
    }
}
