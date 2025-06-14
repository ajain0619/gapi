package com.nexage.app.services.impl;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.aol.crs.cdk.CdkConfig;
import com.aol.crs.cdk.SyncedConfig;
import com.aol.crs.cdk.cache.GuavaBasedLocalCache;
import com.aol.crs.cdk.cache.model.Creative;
import com.aol.crs.model.v1.ContentType;
import com.aol.crs.model.v1.ScanTag;
import com.codahale.metrics.MetricRegistry;
import com.nexage.app.crs.CrsMockHelper;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    locations = {
      "classpath:application-context-test.xml",
      "classpath:application-context-test-crs.xml"
    })
class SellerCreativeContentServiceImplIT {

  @Mock private CdkConfig cdkConfig;
  @Mock private SyncedConfig syncedConfig;
  @Mock private MetricRegistry metricRegistry;

  @Autowired private CrsMockHelper crsMockHelper;

  private CdkClientResource cdkClientResource;
  private SellerCreativeContentServiceImpl sellerCreativeContentService;

  @BeforeEach
  public void setup() throws Exception {
    cdkClientResource = new CdkClientResource(syncedConfig, metricRegistry);
    var crsService = new CrsServiceImpl(cdkClientResource);
    sellerCreativeContentService = new SellerCreativeContentServiceImpl(crsService);

    when(syncedConfig.getSyncedConfig()).thenReturn(cdkConfig);
    when(syncedConfig.getSyncedConfig().getSsoApiEndpoint()).thenReturn("http://127.0.0.1:18096");
    when(syncedConfig.getSyncedConfig().getSsoApiRefreshIntervalMinutes()).thenReturn(10);
    when(syncedConfig.getSyncedConfig().getSsoApiClientId()).thenReturn("ssoApiClientId");
    when(syncedConfig.getSyncedConfig().getSsoApiClientSecret()).thenReturn("crsSsoSecret");
    when(syncedConfig.getSyncedConfig().getCrsApiEndpointRead())
        .thenReturn("http://127.0.0.1:18097/");
    when(syncedConfig.getSyncedConfig().getLockStripesCount()).thenReturn(1);
    when(syncedConfig.getSyncedConfig().getMetricRegistryPrefix()).thenReturn("geneva");
    when(syncedConfig.getSyncedConfig().getDatadogHost()).thenReturn("localhost");
    when(syncedConfig.getSyncedConfig().getDatadogPort()).thenReturn(8);
    when(syncedConfig.getSyncedConfig().isCRSGetRequestsEnabled()).thenReturn(true);

    Class localCacheImplementation = GuavaBasedLocalCache.class;
    when(syncedConfig.getSyncedConfig().getLocalCacheImplementation())
        .thenReturn(localCacheImplementation);

    cdkClientResource.init();
    crsMockHelper.reset();
  }

  @AfterEach
  public void close() {
    cdkClientResource.close();
  }

  @Test
  void testCreativeContentServiceWithNoneCreativeFound() throws IOException {
    // given
    var scanTags = List.of(ScanTag.builder().id(1L).name("attribute1").build());
    var creative =
        Creative.builder()
            .id("12345abcde")
            .adSourceId("11")
            .buyerId("10")
            .buyerCreativeId("2")
            .content("content")
            .contentType(ContentType.DISPLAY_MARKUP)
            .scanTags(scanTags)
            .build();

    // when & then
    crsMockHelper.putToCreativeStore(creative);

    var thrown =
        Assertions.catchThrowable(
            () ->
                await()
                    .pollInterval(1, TimeUnit.SECONDS)
                    .atMost(10, TimeUnit.SECONDS)
                    .until(
                        () -> {
                          sellerCreativeContentService.getCreativeContent(
                              1234L, "a1b2c3"); // invalid ID
                          return true;
                        }));
    assertThat(thrown, instanceOf(GenevaValidationException.class));
  }

  @Test
  void testCreativeContentServiceWithValidResponse() throws IOException {
    // given
    var scanTags =
        List.of(
            ScanTag.builder().id(1L).name("attribute1").build(),
            ScanTag.builder().id(17L).name("attribute2").build(),
            ScanTag.builder().id(18L).name("attribute3").build());
    var creative =
        Creative.builder()
            .id("12345abcde")
            .adSourceId("11")
            .buyerId("10")
            .buyerCreativeId("13")
            .content("content")
            .contentType(ContentType.DISPLAY_MARKUP)
            .scanTags(scanTags)
            .build();

    // when & then
    crsMockHelper.putToCreativeStore(creative);
    with()
        .pollInterval(1, TimeUnit.SECONDS)
        .await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              sellerCreativeContentService.getCreativeContent(1234L, "12345abcde");
              return true;
            });
    var fetchedAdContent = sellerCreativeContentService.getCreativeContent(1234L, "12345abcde");
    assertEquals("content", fetchedAdContent);
  }
}
