package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.IdentityProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/identity-provider-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
public class IdentityProviderRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private IdentityProviderRepository identityProviderRepository;

  @Test
  void shouldFindAllIdentityProviders() {
    // when
    Page<IdentityProvider> page = identityProviderRepository.findAll(PageRequest.of(0, 10));

    // then
    assertEquals(4, page.getTotalElements());
    assertEquals("CONNECTID", page.getContent().get(0).getName());
    assertEquals(1, page.getContent().get(0).getPid());
    assertEquals(false, page.getContent().get(0).getUiVisible());
  }

  @Test
  void shouldFindSpecifiedIdentityProvider() {
    // when
    IdentityProvider identityProvider = identityProviderRepository.findById(1L).orElseThrow();

    // then
    assertNotNull(identityProvider);
    assertEquals("CONNECTID", identityProvider.getName());
    assertEquals(1, identityProvider.getPid());
    assertEquals(false, identityProvider.getUiVisible());
  }
}
