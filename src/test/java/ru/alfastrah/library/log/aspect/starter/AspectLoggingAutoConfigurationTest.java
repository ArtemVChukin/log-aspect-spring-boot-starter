package ru.alfastrah.library.log.aspect.starter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AspectLoggingAutoConfiguration.class})
class AspectLoggingAutoConfigurationTest {

    @DisplayName("Тест загрузки конфигурации в контекст")
    @Test
    void contextLoads() {
    }
}