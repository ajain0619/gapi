package com.ssp.geneva.sdk.onecentral.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ssp.geneva.sdk.onecentral.model.Role;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OneCentralSdkConfigTest {

  @Test
  void shouldLoadProdEnvironmentRoles() {
    OneCentralSdkConfig config = new OneCentralSdkConfig();
    ReflectionTestUtils.setField(config, "environment", "prod");
    Set<Role> roles = config.roles();
    assertNotNull(roles);
    Role role = new Role();
    role.setId(74547L);
    role.setName("AdminNexage");
    assertTrue(roles.contains(role));
  }

  @Test
  void shouldNotReturnRolesIfNonExistingEnvironment() {
    OneCentralSdkConfig config = new OneCentralSdkConfig();
    ReflectionTestUtils.setField(config, "environment", "test");
    Set<Role> roles = config.roles();
    assertNotNull(roles);
    assertTrue(roles.isEmpty());
  }
}
