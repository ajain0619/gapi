package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenevaServletContextConfig.class})
class GenevaServletContextConfigIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void shouldRegisterBeans() {
    assertNotNull(applicationContext.getBean("requestContextFilter"));
    assertNotNull(applicationContext.getBean("mdcFilter"));
    assertNotNull(applicationContext.getBean("etagFilter"));
    assertNotNull(applicationContext.getBean("dealSupplierCacheFilter"));
    assertNotNull(applicationContext.getBean("instrumentedFilter"));
    assertNotNull(applicationContext.getBean("securityHeadersFilter"));
    assertNotNull(applicationContext.getBean("multipartProperties"));
  }
}
