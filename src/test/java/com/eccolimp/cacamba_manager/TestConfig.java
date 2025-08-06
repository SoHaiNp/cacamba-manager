package com.eccolimp.cacamba_manager;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;

@TestConfiguration
public class TestConfig {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private SpringTemplateEngine templateEngine;
} 