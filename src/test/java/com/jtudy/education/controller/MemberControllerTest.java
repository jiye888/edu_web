package com.jtudy.education.controller;

import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@Log4j2
@SpringBootTest
class MemberControllerTest {
    
    @Autowired
    private MemberController memberController;

    @Test
    public void localDateTime() {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        Model model = new ExtendedModelMap();
        Page<Object> date = memberController.getMembers(Long.parseLong("1"), 1, model);
        System.out.println(date);
    }


}