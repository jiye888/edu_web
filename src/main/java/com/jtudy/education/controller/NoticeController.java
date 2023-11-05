package com.jtudy.education.controller;

import com.jtudy.education.DTO.*;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.security.SecurityMember;
import com.jtudy.education.service.FileUploadService;
import com.jtudy.education.service.ImageService;
import com.jtudy.education.service.NoticeService;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final FileUploadService fileUploadService;
    private final ImageService imageService;

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @GetMapping("/list")
    public String list(@RequestParam("academy") Long acaNum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NoticeDTO> noticeDTO = noticeService.getAll(acaNum, pageable);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", acaNum);
        return "notice/list";
    }

    @GetMapping("/read")
    public String read(@RequestParam("number") Long notNum, Model model) throws IOException {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        List<FileUploadDTO> fileList = noticeService.getAllFiles(notNum);
        List<ImageDTO> images = noticeService.getAllImages(notNum);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("academy", noticeDTO.getAcaNum());
        model.addAttribute("files", fileList);
        model.addAttribute("image", images);
        return "notice/read";
    }

    @GetMapping("/register")
    public String register(@RequestParam("academy") Long acaNum, @AuthenticationPrincipal SecurityMember member, Model model) {
        if (!noticeService.validateMember(acaNum, member)) {
            model.addAttribute("msg", "관리자 권한이 없습니다."); //*exception
            return "academy/exception";
        } else {
            model.addAttribute("academy", acaNum);
            model.addAttribute("notice", new NoticeFormDTO());
            return "notice/registerForm";
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestPart @Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult, @RequestPart(value = "files", required = false) MultipartFile[] files, @RequestPart(value = "images", required = false) MultipartFile[] images,
                                   @RequestPart(value = "imgArray", required = false) List<ImgArrayDTO> imgArray, @AuthenticationPrincipal SecurityMember member) throws IOException {
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
        if (files != null && files.length > 0) {
            noticeService.registerFile(files, notNum, member.getMember());
        }
        if (imageService.isNotNullOrEmpty(images, imgArray)) {
            noticeService.registerImg(images, imgArray, notNum, member.getMember());
        }
        return ResponseEntity.ok().body(notNum);
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("number") Long notNum, Model model, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        List<FileUploadDTO> files = noticeService.getAllFiles(notNum);
        List<ImageDTO> image = noticeService.getAllImages(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            model.addAttribute("notice", noticeDTO);
            model.addAttribute("files", files);
            model.addAttribute("image", image);
        } else {
            model.addAttribute("msg", "관리자 권한이 없습니다.");
            return "academy/exception";
        }
        return "notice/modifyForm";
    }

    @PostMapping("/modify")
    public ResponseEntity modify(@RequestPart @Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult, @RequestPart(value = "files", required = false) MultipartFile[] files, @RequestPart(value = "images", required = false) MultipartFile[] images,
                                 @RequestPart(value = "imgArray", required = false) List<ImgArrayDTO> imgArray, @RequestPart(value = "existFiles", required = false) List<String> existFiles, @RequestPart(value = "existImgArray", required = false) List<ImgArrayDTO> existImgArray, @AuthenticationPrincipal SecurityMember member) {
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
        try {
            noticeService.updateFile(files, existFiles, notNum, member.getMember());
            noticeService.updateImg(images, imgArray, existImgArray, notNum, member.getMember());
        } catch (IOException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return ResponseEntity.internalServerError().body(e.getClass());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity delete(@RequestParam("number") Long notNum, @AuthenticationPrincipal SecurityMember member) {
        NoticeDTO noticeDTO = noticeService.getOne(notNum);
        if (noticeService.validateMember(noticeDTO.getAcaNum(), member)) {
            noticeService.delete(notNum);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).body("관리자 권한이 없습니다.");
        }
    }

    @GetMapping(value = "/search")
    public String search(@RequestParam(value="academy") Long acaNum, @RequestParam(required = false) String title, @RequestParam(required = false) String content, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        try {
            Map<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("content", content);
            Page<NoticeDTO> notice = noticeService.search(acaNum, map, pageable);
            model.addAttribute("notice", notice);
            model.addAttribute("academy", acaNum);
            return "notice/search";
        } catch (NullPointerException e) {
            String msg = "검색 결과가 없습니다.";
            model.addAttribute("msg", msg);
            return "academy/exception";
        }
    }

}
