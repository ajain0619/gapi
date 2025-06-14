package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/sdk-handshake-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SdkHandshakeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired SdkHandshakeConfigRepository sdkHandshakeConfigRepository;

  @Test
  void shouldVerifyHandshakeKeyExists() {
    assertTrue(sdkHandshakeConfigRepository.existsByHandshakeKey("*"));
  }

  @Test
  void shouldVerifyKeyDoesNotExist() {
    assertFalse(sdkHandshakeConfigRepository.existsByHandshakeKey("somekey"));
  }
}
