package com.ssp.geneva.sdk.xandr.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource("classpath:application-test.properties")
class XandrSdkConfigIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void shouldVerifyApplicationPropertiesSet() {
    final Environment environment = applicationContext.getEnvironment();
    final String endpoint = environment.getProperty("xandr.service.endpoint");

    assertEquals("http://127.0.0.1", endpoint);
  }

  @Test
  void shouldRegisterExpectedBeans() {
    XandrSdkConfigProperties xandrSdkConfigProperties =
        (XandrSdkConfigProperties) applicationContext.getBean("xandrSdkConfigProperties");
    assertNotNull(xandrSdkConfigProperties);

    RestTemplate xandrRestTemplate = (RestTemplate) applicationContext.getBean("xandrRestTemplate");
    assertNotNull(xandrRestTemplate);

    DealRepository dealRepository = (DealRepository) applicationContext.getBean("dealRepository");
    assertNotNull(dealRepository);

    AuthRepository authRepository = (AuthRepository) applicationContext.getBean("authRepository");
    assertNotNull(authRepository);
  }
}
