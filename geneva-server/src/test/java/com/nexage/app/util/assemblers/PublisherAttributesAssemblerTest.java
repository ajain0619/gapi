package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherAttributesAssemblerTest {

  private final int DEFAULT_PUBLISHER_DATA_PROTECTION_ROLE = 0;
  private final int DEFAULT_GDPR_JURISDICTION = 0;

  @Mock private UserContext userContext;
  @Mock private PublisherTagAssembler publisherTagAssembler;
  @Mock private PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;

  @InjectMocks private PublisherAttributesAssembler publisherAttributesAssembler;

  @Spy private SellerAttributes model;

  @Test
  void test_make() {
    final Set<String> fields =
        Set.of(
            "adFeedbackOptOut",
            "sellerType",
            "trafficSourceType",
            "publisherDataProtectionRole",
            "videoUseInboundSiteOrApp",
            "buyerTransparencyOptOut",
            "revenueGroupPid",
            "sellerDomainVerificationAuthLevel",
            "humanOptOut",
            "smartQPSEnabled",
            "externalAdVerificationSamplingRate",
            "creativeSuccessRateThreshold",
            "creativeSuccessRateThresholdOptOut",
            "dynamicFloorEnabled",
            "smartExchangeAttributes",
            "enableCtvSelling",
            "customDealFloorEnabled");

    publisherAttributesAssembler.make(null, model, fields);

    verify(model, times(1)).getAdFeedbackOptOut();
    verify(model, times(1)).getSellerType();

    verify(model, times(1)).getBuyerTransparencyOptOut();
    verify(model, times(1)).getRevenueGroupPid();
    verify(model, times(1)).getPublisherDataProtectionRole();

    verify(model, times(1)).getVideoUseInboundSiteOrApp();

    verify(model, times(1)).getSellerDomainVerificationAuthLevel();

    verify(model, times(1)).getHumanOptOut();
    verify(model, times(1)).getSmartQPSEnabled();
    verify(model, times(1)).getExternalAdVerificationSamplingRate();
    verify(model, times(1)).getDynamicFloorEnabled();
    verify(model).getCreativeSuccessRateThreshold();
    verify(model).isCreativeSuccessRateThresholdOptOut();

    verify(model).getSmartExchangeAttributes();
    verify(model, times(1)).isCustomDealFloorEnabled();
  }

  @Test
  void shouldProducePublisherAttributesWithValuesFromSellerAttributes() {
    final Set<String> fields = Set.of("enableCtvSelling");

    model.setEnableCtvSelling(true);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertTrue(returnedAttributes.isEnableCtvSelling());
  }

  @Test
  void test_makePublisherDataProtectionRole() {
    final Set<String> fields = Set.of("publisherDataProtectionRole");

    model.setPublisherDataProtectionRole(0);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        model.getPublisherDataProtectionRole(),
        returnedAttributes.getPublisherDataProtectionRole());
  }

  @Test
  void test_makePublisherDataProtectionRoleDefaultValue() {
    final Set<String> fields = Set.of("publisherDataProtectionRole");

    model.setPublisherDataProtectionRole(null);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        DEFAULT_PUBLISHER_DATA_PROTECTION_ROLE,
        returnedAttributes.getPublisherDataProtectionRole());
  }

  @Test
  void test_makeVideoUseInboundSiteOrApp() {
    final Set<String> fields = Set.of("videoUseInboundSiteOrApp");

    model.setVideoUseInboundSiteOrApp(true);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        model.getVideoUseInboundSiteOrApp(), returnedAttributes.getVideoUseInboundSiteOrApp());
  }

  @Test
  void test_makeWithBuyerTransparencyOptOut() {
    final Set<String> fields = Set.of("buyerTransparencyOptOut");

    model.setBuyerTransparencyOptOut(true);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        model.getBuyerTransparencyOptOut(), returnedAttributes.getBuyerTransparencyOptOut());
  }

  @Test
  void test_makeWithRevenueGroupPid() {
    final Set<String> fields = Set.of("revenueGroupPid");

    model.setRevenueGroupPid(1L);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(model.getRevenueGroupPid(), returnedAttributes.getRevenueGroupPid());
  }

  @Test
  void test_makeSellerDomainVerificationAuthLevel() {
    final Set<String> fields = Set.of("sellerDomainVerificationAuthLevel");

    model.setSellerDomainVerificationAuthLevel(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED,
        returnedAttributes.getSellerDomainVerificationAuthLevel());
  }

  @Test
  void test_makePublisherAttributeWithExternalAdSamplingRateValueDefaultValue() {
    final Set<String> fields = Set.of("externalAdVerificationSamplingRate");

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(0.0f, returnedAttributes.getExternalAdVerificationSamplingRate());
  }

  @Test
  void test_makePublisherAttributeWithExternalAdSamplingRateValueValidValue() {
    final Set<String> fields = Set.of("externalAdVerificationSamplingRate");

    model.setExternalAdVerificationSamplingRate(60.52f);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(60.52f, returnedAttributes.getExternalAdVerificationSamplingRate());
  }

  @Test
  void test_makePublisherAttributeWithExternalAdSamplingRateValueInvalidValue() {
    final Set<String> fields = Set.of("externalAdVerificationSamplingRate");

    model.setExternalAdVerificationSamplingRate(70f);

    PublisherAttributes pub = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(70f, pub.getExternalAdVerificationSamplingRate());
  }

  @Test
  void test_makeDynamicFloorEnabled() {
    final Set<String> fields = Set.of("dynamicFloorEnabled");

    model.setDynamicFloorEnabled(false);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(model.getDynamicFloorEnabled(), returnedAttributes.getDynamicFloorEnabled());
  }

  @Test
  void test_makeAdStrictApproval() {
    var sellerAttributes = new SellerAttributes();
    sellerAttributes.setAdStrictApproval(true);

    var publisherAttributesDTO = publisherAttributesAssembler.make(null, sellerAttributes);
    assertTrue(publisherAttributesDTO.getAdStrictApproval());
  }

  @Test
  void shouldCreateSmartExchangeAttributesDTOFromModel() {
    Set<String> fields = Set.of("smartExchangeAttributes");

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(true);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes =
        publisherAttributesAssembler.make(null, model, fields);

    SmartExchangeAttributesDTO smartExchangeAttributesDTO =
        publisherAttributes.getSmartExchangeAttributes();
    assertNotNull(smartExchangeAttributesDTO);
    assertEquals(1, smartExchangeAttributesDTO.getVersion());
    assertTrue(smartExchangeAttributesDTO.getSmartMarginEnabled());
  }

  @Test
  void test_makePublisherAttributeWithExternalAdSamplingPolicyKeyDefaultValue() {
    final Set<String> fields = Set.of("externalAdVerificationPolicyKey");

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertNull(returnedAttributes.getExternalAdVerificationPolicyKey());
  }

  @Test
  void test_makePublisherAttributeWithExternalAdPolicyKeyValueValidValue() {
    final Set<String> fields = Set.of("externalAdVerificationPolicyKey");

    model.setExternalAdVerificationPolicyKey("a8a87c69-b518-4868-8e79-45c147356716");

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(
        "a8a87c69-b518-4868-8e79-45c147356716",
        returnedAttributes.getExternalAdVerificationPolicyKey());
  }

  @Test
  void shouldReturnDefaultWhenMakePublisherAttributeWithCreativeSuccessRateThresholdDefaultValue() {
    final Set<String> fields = Set.of("creativeSuccessRateThreshold");

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertNull(returnedAttributes.getCreativeSuccessRateThreshold());
  }

  @Test
  void
      shouldReturnFalseWhenMakePublisherAttributeWithCreativeSuccessRateThresholdOptOutDefaultValue() {
    final Set<String> fields = Set.of("creativeSuccessRateThresholdOptOut");

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertFalse(returnedAttributes.isCreativeSuccessRateThresholdOptOut());
  }

  @Test
  void shouldAddRawResponseToPublisherAttributesDto() {
    PublisherAttributes returnedAttributes =
        publisherAttributesAssembler.make(null, model, Set.of("rawResponse"));

    assertFalse(returnedAttributes.getRawResponse());
  }

  @Test
  void shouldAddHumanPrebidSampleRateToPublisherAttributesDto() {
    final Set<String> fields = Set.of("humanPrebidSampleRate");

    model.setHumanPrebidSampleRate(40);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(model.getHumanPrebidSampleRate(), returnedAttributes.getHumanPrebidSampleRate());
  }

  @Test
  void shouldAddHumanPostbidSampleRateToPublisherAttributesDto() {
    final Set<String> fields = Set.of("humanPostbidSampleRate");

    model.setHumanPostbidSampleRate(60);

    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    assertEquals(model.getHumanPostbidSampleRate(), returnedAttributes.getHumanPostbidSampleRate());
  }

  @Test
  void shouldAddCustomDealFloorEnabledToPublisherAttributesDto() {
    // given
    final Set<String> fields = Set.of("customDealFloorEnabled");
    model.setCustomDealFloorEnabled(true);

    // when
    PublisherAttributes returnedAttributes = publisherAttributesAssembler.make(null, model, fields);

    // then
    assertEquals(model.isCustomDealFloorEnabled(), returnedAttributes.isCustomDealFloorEnabled());
  }
}
