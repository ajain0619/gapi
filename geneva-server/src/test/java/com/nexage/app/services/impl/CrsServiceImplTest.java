package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aol.crs.cdk.CdkClient;
import com.aol.crs.cdk.cache.model.Creative;
import com.aol.crs.model.v1.ContentType;
import com.aol.crs.model.v1.ScanTag;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrsServiceImplTest {

  @Mock CdkClient cdkClient;

  @Mock CdkClientResource cdkClientResource;

  @InjectMocks CrsServiceImpl crsService;

  @BeforeEach
  public void setUp() {
    when(cdkClientResource.getCdkClient()).thenReturn(cdkClient);
  }

  @Test
  void shouldFetchEmptyResultWhenCrsIdNotFound() {
    CompletableFuture<Creative> emptyCreative = mock(CompletableFuture.class);
    when(cdkClient.fetchCreative("123", null, null, null, true)).thenReturn(emptyCreative);
    Optional<Creative> response = crsService.fetchCreative("123");
    assertEquals(response, Optional.empty());
  }

  @Test
  void shouldFetchCreativeWithValidCrsId() throws Exception {
    List<ScanTag> scanTags =
        List.of(
            ScanTag.builder().id(1L).name("attribute1").build(),
            ScanTag.builder().id(17L).name("attribute2").build(),
            ScanTag.builder().id(18L).name("attribute3").build());

    Creative creative =
        Creative.builder()
            .id("1111")
            .adSourceId("11")
            .buyerId("10")
            .buyerCreativeId("13")
            .content("content")
            .contentType(ContentType.DISPLAY_MARKUP)
            .scanTags(scanTags)
            .build();

    CompletableFuture<Creative> crsResponse =
        (CompletableFuture<Creative>) mock(CompletableFuture.class);
    when(cdkClient.fetchCreative("123", null, null, null, true)).thenReturn(crsResponse);
    when(crsResponse.get()).thenReturn(creative);

    Optional<Creative> response = crsService.fetchCreative("123");
    assertEquals(creative, response.get());
  }

  @Test
  void shouldThrowInternalSystemExceptionIfCrsCallFails() {
    when(cdkClient.fetchCreative("123", null, null, null, true))
        .thenThrow(new RuntimeException("Dummy error"));
    var ex = assertThrows(GenevaAppRuntimeException.class, () -> crsService.fetchCreative("123"));
    assertEquals(ServerErrorCodes.SERVER_CRS_INTERNAL_ERROR, ex.getErrorCode());
  }

  @Test
  void shouldFetchEmptyResultIfCrsCallFails() {
    when(cdkClient.fetchCreative("1", "2", "3", null, true, true, true))
        .thenThrow(new RuntimeException("Dummy error"));
    Optional<Creative> response = crsService.fetchCreative("1", "2", "3");
    assertEquals(response, Optional.empty());
  }
}
