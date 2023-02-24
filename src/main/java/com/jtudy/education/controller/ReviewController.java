package com.jtudy.education.controller;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.Map;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public void list(@RequestParam("academy") Long acaNum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "revNum"));
        Page<ReviewDTO> reviewDTO = reviewService.getAll(acaNum, pageable);
        model.addAttribute("academy", acaNum);
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, Model model) {
        model.addAttribute("academy", acaNum);
        model.addAttribute("review", new ReviewFormDTO());
        return "/review/registerForm";
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid ReviewFormDTO reviewFormDTO, @AuthenticationPrincipal SecurityMember member, Model model) {
        try {
            System.out.println(reviewFormDTO);
            Long rev = reviewService.register(reviewFormDTO);
            System.out.println(rev);
        } catch (NullPointerException e) {
            String message = "Null pointer exception";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.status(HttpStatus.SEE_OTHER).body(message);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read")
    public void read(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("academy", reviewDTO.getAcaNum());
        model.addAttribute("review", reviewDTO);
        return "/review/modifyForm";
    }

    @PostMapping("/modify")
    @ResponseBody
    public void modify(@RequestBody @Valid ReviewFormDTO reviewFormDTO, @AuthenticationPrincipal SecurityMember member, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", "모든 항목을 입력해주세요.");
        }
        reviewService.update(reviewFormDTO);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void delete(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("academy", reviewDTO.getAcaNum());
        reviewService.delete(revNum);
    }

    @GetMapping("/by")
    public ResponseEntity reviews(@RequestParam("member") Long memNum, @RequestParam(value = "page", defaultValue = "1") int page,
                                  @AuthenticationPrincipal SecurityMember securityMember, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "revNum");
        try {
            if (memNum.equals(securityMember.getMember().getMemNum())) {
                Page<ReviewDTO> review = reviewService.getReviews(securityMember.getMember(), pageable);
                model.addAttribute("review", review);
            } else {
                String message = "본인이 아닙니다. 권한이 없습니다.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NullPointerException e) {
            String message = "작성된 리뷰가 없습니다.";
            return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, "member/read").body(message);
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
