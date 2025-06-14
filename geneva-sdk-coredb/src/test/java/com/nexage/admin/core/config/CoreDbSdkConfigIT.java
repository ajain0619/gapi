package com.nexage.admin.core.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
class CoreDbSdkConfigIT extends CoreDbSdkIntegrationTestBase {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var coreDbSdkConfigProperties = context.getBean("coreDbSdkConfigProperties");
    assertNotNull(coreDbSdkConfigProperties);
  }
}
