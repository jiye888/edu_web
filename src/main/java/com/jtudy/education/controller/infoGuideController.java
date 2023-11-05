package com.jtudy.education.controller;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.DTO.InfoGuideDTO;
import com.jtudy.education.DTO.InfoGuideFormDTO;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.InfoGuideService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/info")
@RequiredArgsConstructor
public class infoGuideController {

    private final InfoGuideService infoGuideService;
    private final TemplateEngine templateEngine;

    private static final Logger logger = LoggerFactory.getLogger(infoGuideController.class);

    @GetMapping("/list")
    public void list(@RequestParam(value = "page", defaultValue = "1") int page, Model model, @AuthenticationPrincipal SecurityMember member) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InfoGuideDTO> infoDTO = infoGuideService.getAll(pageable);
        model.addAttribute("info", infoDTO);
        if (member != null) {
            boolean isAdmin = member.getMember().getRolesList().contains(Roles.ADMIN);
            model.addAttribute("isAdmin", isAdmin);
        }
    }

    @GetMapping("/register")
    public ResponseEntity register(@AuthenticationPrincipal SecurityMember member, Model model) {
        if (infoGuideService.validateMember(member)) {
            model.addAttribute("info", new InfoGuideFormDTO());
            Context context = new Context();
            context.setVariables(model.asMap());
            String template = templateEngine.process("/info/registerForm", context);
            return ResponseEntity.ok().body(template);
        } else {
            return ResponseEntity.status(401).body("관리자 권한이 없습니다.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestPart @Valid InfoGuideFormDTO infoGuideFormDTO, BindingResult bindingResult, @RequestPart(value = "images", required = false) MultipartFile[] images, @RequestPart(required = false) List<ImgArrayDTO> imgArray,
                                   @AuthenticationPrincipal SecurityMember member) {
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            map.put("BindingResultError", "true");
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }
        Long infoNum = infoGuideService.register(infoGuideFormDTO, member.getMember());
        try {
            infoGuideService.registerImg(images, imgArray, infoNum, member.getMember());
            return ResponseEntity.ok().body(infoNum);
        } catch (IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            String msg = "알림글 이미지 등록에 실패했습니다.";
            return ResponseEntity.internalServerError().body(msg);
        }
    }

    @GetMapping("/read")
    public void read(@RequestParam("number") Long infoNum, Model model) {
        InfoGuideDTO infoGuideDTO = infoGuideService.getOne(infoNum);
        model.addAttribute("info", infoGuideDTO);
        List<ImageDTO> imageDTO = infoGuideService.getAllImages(infoNum);
        model.addAttribute("image", imageDTO);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long infoNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        InfoGuideDTO infoGuideDTO = infoGuideService.getOne(infoNum);
        List<ImageDTO> image = infoGuideService.getAllImages(infoNum);
        if (infoGuideService.validateMember(member)) {
            model.addAttribute("image", image);
            model.addAttribute("info", infoGuideDTO);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "academy/exception";
        }
        return "info/modifyForm";
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestPart @Valid InfoGuideFormDTO infoGuideFormDTO, BindingResult bindingResult, @RequestPart(value = "images", required = false) MultipartFile[] images,
                                 @RequestPart(required = false) List<ImgArrayDTO> imgArray, @RequestPart(required = false) List<ImgArrayDTO> existImgArray, @AuthenticationPrincipal SecurityMember member) {
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            map.put("BindingResultError", "true");
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                map.put(fieldError.getField()+"Error", fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }
        Long infoNum = infoGuideService.update(infoGuideFormDTO);
        try {
            infoGuideService.updateImg(images, imgArray, existImgArray, infoNum, member.getMember());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            String msg = "알림글 이미지 수정에 실패했습니다.";
            return ResponseEntity.internalServerError().body(msg);
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity delete(@RequestParam("number") Long infoNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        InfoGuideDTO infoGuideDTO = infoGuideService.getOne(infoNum);
        if (infoGuideService.validateMember(member)) {
            try {
                infoGuideService.delete(infoNum);
            } catch (IOException e) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
                String msg = "알림글 삭제에 실패했습니다.";
                return ResponseEntity.internalServerError().body(msg);
            }
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).body("관리자 권한이 없습니다.");
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String title, @RequestParam(required = false) String content, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        try {
            Map<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("content", content);
            Page<InfoGuideDTO> info = infoGuideService.search(map, pageable);
            model.addAttribute("info", info);
            return "info/search";
        } catch (Exception e) {
            String msg = e.getMessage();
            model.addAttribute("msg", msg);
            return "academy/exception";
        }
    }
}
