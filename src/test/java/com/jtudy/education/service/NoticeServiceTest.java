package com.jtudy.education.service;

import groovy.util.logging.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class NoticeServiceTest {

    @Autowired
    NoticeService noticeService;

    public void view() {

        noticeService.getOne(Long.parseLong("1"));
    }

}