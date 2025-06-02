package com.example.levarehulticops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@Configuration
public class ThymeleafConfig {

    private final ITemplateResolver templateResolver;

    public ThymeleafConfig(ITemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        // регистрируем Java8TimeDialect, чтобы #temporals.now() заработало:
        engine.addDialect(new Java8TimeDialect());
        return engine;
    }
}
