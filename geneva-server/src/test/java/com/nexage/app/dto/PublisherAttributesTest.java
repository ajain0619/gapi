package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.sparta.jpa.model.SellerType;
import com.nexage.app.dto.publisher.PublisherAttributes;
import org.junit.jupiter.api.Test;

class PublisherAttributesTest {

  @Test
  void shouldReturnAdFeedbackOptOut() {
    PublisherAttributes attrib = new PublisherAttributes();
    assertNotNull(attrib.getAdFeedbackOptOut());
  }

  @Test
  void shouldReturnPublisherWithAdFeedbackOptOut() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withAdFeedbackOptOut(true);
    PublisherAttributes publisherAttributes = builder.build();
    assertTrue(publisherAttributes.getAdFeedbackOptOut());

    builder.withAdFeedbackOptOut(false);
    publisherAttributes = builder.build();
    assertFalse(publisherAttributes.getAdFeedbackOptOut());
  }

  @Test
  void shouldReturnBuyerTransparencyOptOut() {
    PublisherAttributes attrib = new PublisherAttributes();
    assertNotNull(attrib.getBuyerTransparencyOptOut());
  }

  @Test
  void shouldReturnPublisherWithBuyerTransparencyOptOut() {

    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();

    builder.withBuyerTransparencyOptOut(true);
    PublisherAttributes publisherAttributes = builder.build();
    assertTrue(publisherAttributes.getBuyerTransparencyOptOut());

    builder.withBuyerTransparencyOptOut(false);
    publisherAttributes = builder.build();
    assertFalse(publisherAttributes.getBuyerTransparencyOptOut());
  }

  @Test
  void shouldReturnRevenueGroupPid() {
    PublisherAttributes attrib = new PublisherAttributes();
    attrib.setRevenueGroupPid(1L);
    assertEquals(1L, attrib.getRevenueGroupPid().longValue());
  }

  @Test
  void shouldReturnPublisherWithRevenueGroupPid() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withRevenueGroupPid(1L);
    PublisherAttributes publisherAttributes = builder.build();
    assertEquals(1L, publisherAttributes.getRevenueGroupPid().longValue());
  }

  @Test
  void shouldReturnSellerType() {
    PublisherAttributes attrib = new PublisherAttributes();
    assertNull(attrib.getSellerType());
  }

  @Test
  void shouldReturnPublisherWithSellerType() {

    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withSellerType(SellerType.DIRECT);
    PublisherAttributes publisherAttributes = builder.build();
    assertEquals(SellerType.DIRECT, publisherAttributes.getSellerType());

    builder.withSellerType(SellerType.INTERMEDIARY);
    publisherAttributes = builder.build();
    assertEquals(SellerType.INTERMEDIARY, publisherAttributes.getSellerType());

    builder.withSellerType(SellerType.BOTH);
    publisherAttributes = builder.build();
    assertEquals(SellerType.BOTH, publisherAttributes.getSellerType());
  }

  @Test
  void shouldReturnPublisherWithSellerDomainVerificationAuthLevel() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withSellerDomainVerificationAuthLevel(
        SellerDomainVerificationAuthLevel.ALLOW_ONLY_AUTHORIZED);
    PublisherAttributes publisherAttributes = builder.build();
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_ONLY_AUTHORIZED,
        publisherAttributes.getSellerDomainVerificationAuthLevel());
  }

  @Test
  void shouldReturnHumanOptOut() {
    PublisherAttributes attrib = new PublisherAttributes();
    assertNotNull(attrib.getHumanOptOut());
  }

  @Test
  void shouldReturnPublisherWithHumanOptOut() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withHumanOptOut(true);
    PublisherAttributes publisherAttributes = builder.build();
    assertTrue(publisherAttributes.getHumanOptOut());

    builder.withHumanOptOut(false);
    publisherAttributes = builder.build();
    assertFalse(publisherAttributes.getHumanOptOut());
  }

  @Test
  void shouldReturnSmartQPSEnabled() {
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    assertNotNull(publisherAttributes.getSmartQPSEnabled());
  }

  @Test
  void shouldReturnWithSmartQPSEnabled() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withSmartQPSEnabled(true);
    PublisherAttributes publisherAttributes = builder.build();
    assertTrue(publisherAttributes.getSmartQPSEnabled());

    builder.withSmartQPSEnabled(false);
    publisherAttributes = builder.build();
    assertFalse(publisherAttributes.getSmartQPSEnabled());
  }

  @Test
  void withVideoUseInboundSiteOrApp() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    builder.withVideoUseInboundSiteOrApp(true);
    PublisherAttributes publisherAttributes = builder.build();
    assertTrue(publisherAttributes.getVideoUseInboundSiteOrApp());

    builder.withVideoUseInboundSiteOrApp(false);
    publisherAttributes = builder.build();
    assertFalse(publisherAttributes.getVideoUseInboundSiteOrApp());
  }

  @Test
  void shouldReturnWithDefaultBiddersAllowList() {
    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();

    builder.withDefaultBiddersAllowList(false);
    PublisherAttributes publisherAttributes = builder.build();
    assertFalse(publisherAttributes.isDefaultBiddersAllowList());

    builder.withDefaultBiddersAllowList(true);
    publisherAttributes = builder.build();
    assertTrue(publisherAttributes.isDefaultBiddersAllowList());
  }
}
