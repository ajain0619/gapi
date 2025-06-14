package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.IdentityProvider;
import com.nexage.admin.core.repository.IdentityProviderRepository;
import com.nexage.app.dto.IdentityProviderDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class IdentityProviderServiceImplTest {

  @Mock private IdentityProviderRepository identityProviderRepository;

  @Mock private Pageable pageable;

  @InjectMocks private IdentityProviderServiceImpl identityProviderService;

  Page<IdentityProvider> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity =
        new PageImpl<IdentityProvider>(TestObjectsFactory.gimme(1, IdentityProvider.class))
            .map(
                identityProvider -> {
                  identityProvider.setPid(3L);
                  identityProvider.setVersion(1);
                  identityProvider.setName("MICROSOFT");
                  identityProvider.setDisplayName("muId");
                  identityProvider.setProviderId(1);
                  identityProvider.setDomain("microsoft.com");
                  identityProvider.setEnabled(true);
                  identityProvider.setUiVisible(true);
                  return identityProvider;
                });
  }

  @Test
  void testGetAllIdentityProviders() {
    when(identityProviderRepository.findAll(any(Pageable.class))).thenReturn(pagedEntity);
    Page<IdentityProviderDTO> returnedPage =
        identityProviderService.getAllIdentityProviders(pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("MICROSOFT", returnedPage.stream().findFirst().get().getName());
    assertEquals(3L, returnedPage.stream().findFirst().get().getPid());
    assertEquals("muId", returnedPage.stream().findFirst().get().getDisplayName());
    assertEquals("microsoft.com", returnedPage.stream().findFirst().get().getDomain());
    assertEquals(1, returnedPage.stream().findFirst().get().getProviderId());
    assertEquals(1, returnedPage.stream().findFirst().get().getVersion());
    assertTrue(returnedPage.stream().findFirst().get().getEnabled());
    assertTrue(returnedPage.stream().findFirst().get().getUiVisible());
  }
}
