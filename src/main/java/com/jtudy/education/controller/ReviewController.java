package com.jtudy.education.controller;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.AcademyMemberService;
import com.jtudy.education.service.ImageService;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AcademyMemberService academyMemberService;
    private final ImageService imageService;
    private final TemplateEngine templateEngine;

    @GetMapping("/list")
    public void list(@RequestParam("academy") Long acaNum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "revNum"));
        Page<ReviewDTO> reviewDTO = reviewService.getAll(acaNum, pageable);
        model.addAttribute("academy", acaNum);
        model.addAttribute("review", reviewDTO);
    }

    @GetMapping("/register")
    public ResponseEntity register(@RequestParam("academy") Long acaNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        try {
            if (!academyMemberService.isPresent(member.getMember().getMemNum(), acaNum)) {
                String msg = "not_member";
                return ResponseEntity.badRequest().body(msg);
            }
            if (reviewService.getByAcademy(acaNum, member.getUsername()) == null) {
                model.addAttribute("academy", acaNum);
                model.addAttribute("review", new ReviewFormDTO());
                Context context = new Context();
                context.setVariables(model.asMap());
                String template = templateEngine.process("review/registerForm", context);
                return ResponseEntity.ok().body(template);
            } else if (reviewService.getByAcademy(acaNum, member.getUsername()).getRevNum() == null) {
                return ResponseEntity.badRequest().body("null"); //*exception
            } else {
                ReviewDTO reviewDTO = reviewService.getByAcademy(acaNum, member.getUsername());
                Long revNum = reviewDTO.getRevNum();
                Map<String, Long> map = new HashMap<>();
                map.put("number", revNum);
                return ResponseEntity.ok().body(map);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestPart @Valid ReviewFormDTO reviewFormDTO, BindingResult bindingResult, @RequestPart MultipartFile[] images, @RequestPart List<List<String>> imgArray,
                                   @AuthenticationPrincipal SecurityMember member, Model model) {
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
            Long revNum = reviewService.register(reviewFormDTO, member.getMember()); //files
            reviewService.registerImg(images, imgArray, revNum, member.getMember());
            return ResponseEntity.ok().body(revNum);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity modify(@RequestBody @Valid ReviewFormDTO reviewFormDTO, BindingResult bindingResult, @AuthenticationPrincipal SecurityMember member, Model model) {
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
            reviewService.update(reviewFormDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void delete(@RequestParam("number") Long revNum, Model model) {
        ReviewDTO reviewDTO = reviewService.getOne(revNum);
        model.addAttribute("academy", reviewDTO.getAcaNum());
        reviewService.delete(revNum);
    }

    @GetMapping("/by")
    public ResponseEntity reviews(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @AuthenticationPrincipal SecurityMember member, Model model, HttpServletRequest request, HttpServletResponse response) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "revNum");
        try {
            Page<ReviewDTO> review = reviewService.getReviews(member.getMember(), pageable);
            model.addAttribute("review", review);

            //model.addAttribute("number", member.getMember().getMemNum());
            WebContext context = new WebContext(request, response, request.getServletContext());
            context.setVariables(model.asMap());
            String template = templateEngine.process("review/by", context);
            return ResponseEntity.status(HttpStatus.OK).body(template);
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

}
