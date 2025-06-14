package com.nexage.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.app.dto.pub.self.serve.PubSelfServeTagMetricsDTO;
import org.junit.jupiter.api.Test;

class PubSelfServeTagMetricsTest {

  @Test
  void shouldCreateAdSourceLogoUrl() {
    performTestCreateAdSourceLogoUrl(
        "http://www.oath.com/images/", "logo.jpg", "http://www.oath.com/images/logo.jpg");
    performTestCreateAdSourceLogoUrl("http://www.oath.com/images/", null, null);
    performTestCreateAdSourceLogoUrl(null, "logo.jpg", null);
    performTestCreateAdSourceLogoUrl(null, null, null);
  }

  private void performTestCreateAdSourceLogoUrl(
      String baseLogoUrl, String logo, String expectedLogoUrl) {
    TagPubSelfServeView pubTag = mock(TagPubSelfServeView.class);
    when(pubTag.getAdsource()).thenReturn(mock(AdsourcePubSelfServeView.class));
    when(pubTag.getEcpmProvision()).thenReturn(TagPubSelfServeView.EcpmProvision.Auto.name());

    Site siteDTO = mock(Site.class);

    Tag tag = mock(Tag.class);
    when(tag.getBuyerLogo()).thenReturn(logo);

    PubSelfServeTagMetricsDTO pubSelfServeTagMetrics =
        new PubSelfServeTagMetricsDTO(pubTag, siteDTO, tag, baseLogoUrl);

    assertEquals(expectedLogoUrl, pubSelfServeTagMetrics.getAdSourceLogoUrl());
  }

  @Test
  void shouldNotThrowNullPointerExceptionWhenNullTagInConstructor() {
    TagPubSelfServeView pubTag = mock(TagPubSelfServeView.class);
    when(pubTag.getAdsource()).thenReturn(mock(AdsourcePubSelfServeView.class));
    when(pubTag.getEcpmProvision()).thenReturn(TagPubSelfServeView.EcpmProvision.Auto.name());

    Site siteDTO = mock(Site.class);

    PubSelfServeTagMetricsDTO pubSelfServeTagMetricsDTO =
        new PubSelfServeTagMetricsDTO(pubTag, siteDTO, null, "logo.jpg");
    assertNotNull(pubSelfServeTagMetricsDTO);
  }
}
