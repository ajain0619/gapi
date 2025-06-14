package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdSourceServiceImplTest {

  @Mock private UserContext userContext;
  @Mock private AdSourceRepository adSourceRepository;

  private AdSourceServiceImpl adSourceService;

  @BeforeEach
  void setUp() {
    adSourceService = new AdSourceServiceImpl(userContext, adSourceRepository);
  }

  @Test
  void shouldFailOnBadTierParam() {
    Map<Long, Long> tagPidBuyerPidMap = Collections.emptyMap();
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> adSourceService.getAdSourcesUsedForTierTags(tagPidBuyerPidMap));

    assertNotNull(exception);
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldReturnValuesWithEmptyTierTags() {
    when(userContext.isOcAdminNexage()).thenReturn(true);
    var result = adSourceService.getAdSourcesUsedForTierTags(Collections.emptyMap());
    assertNotNull(result);
  }

  @Test
  void shouldReturnValuesWithoutMatching() {

    when(userContext.isOcAdminNexage()).thenReturn(true);
    var result = adSourceService.getAdSourcesUsedForTierTags(Map.of(1L, 1L));
    assertNotNull(result);

    final var buyerPid = new Random().nextLong();
    final var tagPid = new Random().nextLong();
    var adSource = new AdSource();
    adSource.setPid(buyerPid);
    when(adSourceRepository.findNonDeletedByPidIn(any())).thenReturn(List.of(adSource));
    result = adSourceService.getAdSourcesUsedForTierTags(Map.of(tagPid, buyerPid));
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.containsKey(tagPid));
  }

  @Test
  void shouldReturnValuesWithTierTags() {

    final var buyerPid = new Random().nextLong();
    final var tagPid = new Random().nextLong();
    var adSource = new AdSource();
    adSource.setPid(buyerPid);
    when(adSourceRepository.findNonDeletedByPidIn(any())).thenReturn(List.of(adSource));
    when(userContext.isOcAdminNexage()).thenReturn(true);
    var result = adSourceService.getAdSourcesUsedForTierTags(Map.of(tagPid, buyerPid));
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.containsKey(tagPid));
  }

  @Test
  void shouldSoftDeleteAdSource() {
    when(adSourceRepository.findById(any())).thenReturn(Optional.of(new AdSource()));
    final long pid = new Random().nextLong();

    adSourceService.softDelete(pid);

    verify(adSourceRepository).save(argThat(adSource -> adSource.getStatus() == Status.DELETED));
  }

  @Test
  void shouldGetAdSourceSummariesByCompanyPid() {
    when(adSourceRepository.findNonDeletedByCompanyPid(any()))
        .thenReturn(List.of(new AdSource(), new AdSource()));
    final long companyPid = new Random().nextLong();

    List<AdSourceSummaryDTO> adSourceSummaryDTOList =
        adSourceService.getAdSourceSummariesByCompanyPid(companyPid);

    assertEquals(2, adSourceSummaryDTOList.size());
  }

  @Test
  void shouldGetAdSourceSummariesForGeneva() {
    when(adSourceRepository.findAllActiveOrderedByName())
        .thenReturn(List.of(new AdSource(), new AdSource()));

    List<AdSourceSummaryDTO> adSourceSummaryDTOList =
        adSourceService.getAdSourceSummariesForGeneva();

    assertEquals(2, adSourceSummaryDTOList.size());
  }
}
