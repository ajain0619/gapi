package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.common.collect.Sets;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.EntitlementService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EntitlementServiceImplTest {

  @Mock SpringUserDetails springUserDetails;
  @Mock UserContext userContext;
  @Autowired EntitlementService entitlementService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    entitlementService = new EntitlementServiceImpl(userContext);
  }

  @Test
  void shouldReturnSuccessfullyGetEntitlements() {

    Set<String> qf = Sets.newHashSet("status");
    String qt = "current";

    Entitlement entitlement =
        new Entitlement(1, "name", "displayName", "application", "type", "permission");

    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    when(springUserDetails.getEntitlements()).thenReturn(Collections.singletonList(entitlement));

    Page<Entitlement> entitlementPage =
        entitlementService.getEntitlements(qt, qf, Pageable.unpaged());

    assertEquals(1, entitlementPage.getTotalElements());
  }

  @Test
  void shouldFailOnGetEntitlements() {
    Pageable pageable = Pageable.unpaged();
    assertThrows(
        GenevaValidationException.class,
        () -> entitlementService.getEntitlements(null, null, pageable));
  }

  @Test
  void shouldReturnSuccessfullyGetEntitlementsEmpty() {

    Set<String> qf = Sets.newHashSet("other");
    String qt = "other";

    Page<Entitlement> entitlementPage =
        entitlementService.getEntitlements(qt, qf, Pageable.unpaged());

    assertTrue(entitlementPage.isEmpty());
  }
}
