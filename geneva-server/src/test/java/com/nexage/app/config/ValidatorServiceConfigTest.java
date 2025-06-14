package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

class ValidatorServiceConfigTest {

  ValidatorServiceConfig validatorServiceConfig;

  @BeforeEach
  public void before() {
    LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    localValidatorFactoryBean.afterPropertiesSet();

    validatorServiceConfig = new ValidatorServiceConfig(localValidatorFactoryBean);
  }

  @Test
  void testValidationPostProcessor() {
    assertNotNull(validatorServiceConfig.validationPostProcessor());
    assert (validatorServiceConfig
        .validationPostProcessor()
        .getClass()
        .isAssignableFrom(MethodValidationPostProcessor.class));
  }
}
