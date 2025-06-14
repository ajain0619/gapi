package com.ssp.geneva.sdk.ckms.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CkmsSdkConfig.class})
@TestPropertySource(
    properties = {
      "ckms.athens.cert.path=/usr",
      "ckms.athens.key.path=/local",
      "ckms.default.keygroups.list=mygroup",
      "ckms.ykeykey.mock=true",
      "ckms.ykeykey.environment=dev",
      "ckms.trust.store.password.encoded=whatever_pass",
      "ckms.connection.retry.count=1",
      "ckms.connection.timeout.ms=12345"
    })
class CkmsSdkConfigIT {

  @Autowired ApplicationContext context;

  @Test
  @DisplayName("Should set property accordingly")
  void shouldSetPropertyAccordingly() {
    CkmsSdkConfigProperties properties =
        (CkmsSdkConfigProperties) context.getBean("ckmsSdkConfigProperties");
    assertNotNull(properties);
    Object mockYKeyKey = ReflectionTestUtils.getField(properties, "mockYKeyKey");
    assertNotNull(mockYKeyKey);
    assertEquals(true, mockYKeyKey);
    Object certPath = ReflectionTestUtils.getField(properties, "certPath");
    assertNotNull(certPath);
    assertEquals("/usr", certPath);
    Object keyPath = ReflectionTestUtils.getField(properties, "keyPath");
    assertNotNull(keyPath);
    assertEquals("/local", keyPath);
    Object yKeyKeyEnvironment = ReflectionTestUtils.getField(properties, "yKeyKeyEnvironment");
    assertNotNull(yKeyKeyEnvironment);
    assertEquals("dev", yKeyKeyEnvironment);
    Object defaultKeyGroups = ReflectionTestUtils.getField(properties, "defaultKeyGroups");
    assertNotNull(defaultKeyGroups);
    assertEquals("mygroup", defaultKeyGroups);
    Object trustStorePassword = ReflectionTestUtils.getField(properties, "trustStorePassword");
    assertNotNull(trustStorePassword);
    assertEquals("whatever_pass", trustStorePassword);
    Object connectionRetryCount = ReflectionTestUtils.getField(properties, "connectionRetryCount");
    assertNotNull(connectionRetryCount);
    assertEquals("1", connectionRetryCount);
    Object connectionTimeout = ReflectionTestUtils.getField(properties, "connectionTimeout");
    assertNotNull(connectionTimeout);
    assertEquals("12345", connectionTimeout);
  }

  @Test
  @DisplayName("Should register expected beans")
  void shouldRegisterExpectedBeans() {
    CkmsProvider ckmsProvider = (CkmsProvider) context.getBean("ckmsProvider");
    assertNotNull(ckmsProvider);

    CkmsSdkClient ckmsSdkClient = (CkmsSdkClient) context.getBean("ckmsSdkClient");
    assertNotNull(ckmsSdkClient);
  }
}
