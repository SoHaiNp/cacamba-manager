package com.eccolimp.cacamba_manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
class CacambaManagerApplicationTests {

    @Test
    void contextLoads() {
    }
}
