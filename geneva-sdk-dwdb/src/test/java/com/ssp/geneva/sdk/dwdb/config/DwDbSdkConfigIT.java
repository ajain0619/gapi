package com.ssp.geneva.sdk.dwdb.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.dwdb.DwDbSdkIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class DwDbSdkConfigIT extends DwDbSdkIntegrationTestBase {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var dwDbSdkConfigProperties = context.getBean("dwDbSdkConfigProperties");
    assertNotNull(dwDbSdkConfigProperties);
  }
}
