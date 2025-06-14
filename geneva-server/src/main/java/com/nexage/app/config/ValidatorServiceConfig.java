package com.nexage.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidatorServiceConfig {

  private final LocalValidatorFactoryBean beanValidator;

  public ValidatorServiceConfig(LocalValidatorFactoryBean beanValidator) {
    this.beanValidator = beanValidator;
  }

  @Bean
  public MethodValidationPostProcessor validationPostProcessor() {
    var postProcessor = new MethodValidationPostProcessor();
    postProcessor.setValidator(beanValidator.getValidator());
    return postProcessor;
  }
}
