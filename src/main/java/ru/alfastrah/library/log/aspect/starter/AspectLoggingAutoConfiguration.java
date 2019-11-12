package ru.alfastrah.library.log.aspect.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.alfastrah.library.log.aspect.lib.AspectLogging;

@Configuration
@ConditionalOnClass(AspectLogging.class)
@EnableAspectJAutoProxy
public class AspectLoggingAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AspectLogging aspectLogging() {
        return new AspectLogging();
    }
}
