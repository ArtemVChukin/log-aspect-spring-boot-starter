package ru.alfastrah.library.log.aspect.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ConditionalOnClass(AspectLogging.class)
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AspectLogProperties.class)
public class AspectLoggingAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(AspectLogging.class)
    @ConditionalOnProperty(name = "library.log.aspect.enable", havingValue = "true")
    AspectLogging aspectLogging() {
        return new AspectLogging();
    }
}
