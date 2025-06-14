package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.context.CompanyContext;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.publisher.BasePublisherAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasePublisherAssemblerTest {

  @Mock private Company companyMock;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private InternalPublisherAttributesAssembler internalPublisherAttributesAssembler;
  @Mock private PublisherDTO publisherMock;
  @Mock private PublisherEligibleBiddersAssembler publisherEligibleBiddersAssembler;
  @Mock private UserContext userContext;

  @Spy Company company;

  private BasePublisherAssembler publisherAssembler;

  @BeforeEach
  void setup() {
    publisherAssembler =
        new BasePublisherAssembler(
            publisherEligibleBiddersAssembler, hbPartnerRepository, userContext) {
          @Override
          protected PublisherAttributesAssembler getPublisherAttributesAssembler() {
            return internalPublisherAttributesAssembler;
          }
        };
  }

  @Test
  void test_applyHbPartnerAttributes_nullDTO() {
    PublisherDTO publisher = new PublisherDTO();
    publisher.setHbPartnerAttributes(null);
    Company company = publisherAssembler.applyHbPartnerAttributes(companyMock, publisher);
    assertEquals(
        Collections.emptySet(), company.getHbPartnerCompany(), "Hb Partner should be empty");
  }

  @Test
  void test_applyHbPartnerAttributes() {
    publisherAssembler.applyHbPartnerAttributes(companyMock, publisherMock);
    verify(companyMock, times(1)).setHbPartnerCompany(any());
  }

  @Test
  void test_publisherDataProtectionRoleFieldUpdate() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setPublisherDataProtectionRole(0);
    publisherAttributes.setRevenueShare(BigDecimal.ZERO);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getPublisherDataProtectionRole(),
        company.getSellerAttributes().getPublisherDataProtectionRole());
  }

  @Test
  void make_makesCorrectDTO() {
    Long pid = 1L;
    Integer version = 2;
    String id = "id";
    String name = "name";
    SellerAttributes sellerAttributesMock = mock(SellerAttributes.class);
    PublisherAttributes publisherAttributesMock = mock(PublisherAttributes.class);
    Set<SellerEligibleBidders> sellerEligibleBiddersSet = Collections.emptySet();
    Set<PublisherEligibleBiddersDTO> publisherEligibleBiddersDTOSet = Collections.emptySet();
    String currency = "currency";

    when(companyMock.getPid()).thenReturn(pid);
    when(companyMock.getVersion()).thenReturn(version);
    when(companyMock.getId()).thenReturn(id);
    when(companyMock.getName()).thenReturn(name);
    when(companyMock.getVersion()).thenReturn(version);
    when(companyMock.getSellerAttributes()).thenReturn(sellerAttributesMock);
    when(companyMock.getEligibleBidders()).thenReturn(sellerEligibleBiddersSet);
    when(companyMock.getCurrency()).thenReturn(currency);

    when(internalPublisherAttributesAssembler.make(
            any(NullableContext.class), eq(sellerAttributesMock)))
        .thenReturn(publisherAttributesMock);
    when(publisherEligibleBiddersAssembler.make(
            any(CompanyContext.class), eq(sellerEligibleBiddersSet)))
        .thenReturn(publisherEligibleBiddersDTOSet);

    PublisherDTO publisherDTO = publisherAssembler.make(null, companyMock);

    assertEquals(pid, publisherDTO.getPid());
    assertEquals(version, publisherDTO.getVersion());
    assertEquals(pid, publisherDTO.getPid());
    assertEquals(name, publisherDTO.getName());
    assertEquals(publisherAttributesMock, publisherDTO.getAttributes());
    assertEquals(publisherEligibleBiddersDTOSet, publisherDTO.getEligibleBidderGroups());
    assertEquals(currency, publisherDTO.getCurrency());
    assertNotNull(publisherDTO.getHbPartnerAttributes());
  }

  @Test
  void test_sellerDomainVerificationAuthLevelFieldUpdate() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setSellerDomainVerificationAuthLevel(
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER);
    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getSellerDomainVerificationAuthLevel(),
        company.getSellerAttributes().getSellerDomainVerificationAuthLevel());
  }

  @Test
  void test_thirdPartyFraudDetectionEnabledMake() {
    Set<String> fields = Set.of("thirdPartyFraudDetectionEnabled");

    publisherAssembler.make(null, company, fields);

    verify(company, times(1)).getThirdPartyFraudDetectionEnabled();
  }

  @Test
  void shouldMakeWithEstimatedTimeRemainingWhenPresent() {
    // given
    var fields = Set.of("estimatedTimeRemain");

    // when
    publisherAssembler.make(null, company, fields);

    // then
    verify(company, times(1)).getEstimatedTimeRemaining();
  }

  @Test
  void test_videoUseInboundSiteOrAppUpdate() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setVideoUseInboundSiteOrApp(true);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getVideoUseInboundSiteOrApp(),
        company.getSellerAttributes().getVideoUseInboundSiteOrApp());
  }

  @Test
  void shouldReturnCorrectlyWhenDefaultBidderAllowListSet() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setDefaultBiddersAllowList(false);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().isDefaultBiddersAllowList(),
        company.getSellerAttributes().isDefaultBiddersAllowList());

    publisherAttributes.setDefaultBiddersAllowList(true);

    publisher.setAttributes(publisherAttributes);

    company = publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().isDefaultBiddersAllowList(),
        company.getSellerAttributes().isDefaultBiddersAllowList());
  }

  @Test
  void shouldReturnFalseWhenDefaultBidderAllowListSetDefault() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setDefaultBiddersAllowList(false);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().isDefaultBiddersAllowList(),
        company.getSellerAttributes().isDefaultBiddersAllowList());
  }

  @Test
  void shouldReturnDefaultValueWhenDefaultValue() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().isDefaultBiddersAllowList(),
        company.getSellerAttributes().isDefaultBiddersAllowList());
  }

  @Test
  void test_sellerAttributeExternalAdVerificationSamplingRateFieldUpdateWithDefaultValue() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setRevenueShare(BigDecimal.ZERO);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(0f, company.getSellerAttributes().getExternalAdVerificationSamplingRate());
  }

  @Test
  void test_sellerAttributeExternalAdVerificationSamplingRateFieldUpdateWithValidValue() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setRevenueShare(BigDecimal.ZERO);
    publisherAttributes.setExternalAdVerificationSamplingRate(20.50f);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getExternalAdVerificationSamplingRate(),
        company.getSellerAttributes().getExternalAdVerificationSamplingRate());

    publisherAttributes.setExternalAdVerificationSamplingRate(0f);

    publisher.setAttributes(publisherAttributes);

    company = publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getExternalAdVerificationSamplingRate(),
        company.getSellerAttributes().getExternalAdVerificationSamplingRate());
  }

  @Test
  void test_sellerAttributeExternalAdVerificationSamplingRateFieldUpdateWithInvalidValue() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setRevenueShare(BigDecimal.ZERO);
    publisherAttributes.setExternalAdVerificationSamplingRate(101f);

    publisher.setAttributes(publisherAttributes);

    Company company = new Company();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    assertEquals(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE, exception.getErrorCode());

    publisherAttributes.setExternalAdVerificationSamplingRate(-0.01f);

    publisher.setAttributes(publisherAttributes);

    exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    assertEquals(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE, exception.getErrorCode());
  }

  @Test
  void
      shouldThrowErrorWhenSellerAttributeCreativeSuccessRateThresholdFieldUpdateWithInvalidUpperLimit() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setRevenueShare(BigDecimal.ZERO);
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(101));

    publisher.setAttributes(publisherAttributes);

    Company company = new Company();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void
      shouldThrowErrorWhenSellerAttributeCreativeSuccessRateThresholdFieldUpdateWithInvalidLowerLimit() {
    // Given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();

    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal("-0.01"));

    publisher.setAttributes(publisherAttributes);

    // When
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    // Then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowErrorWhenSellerAttributeCreativeSuccessRateThresholdFieldUpdateWithOptOut() {
    // Given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(10));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(true);

    publisher.setAttributes(publisherAttributes);
    // when creative threshold is opt-out we validate publisherAttributes DTO
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    // then error should be thrown as it creativeSuccessRateThreshold should not allow values
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_THRESHOLD_NOT_ALLOWED,
        exception.getErrorCode());
  }

  @Test
  void shouldSuccessWhenSellerAttributeCreativeSuccessRateThresholdFieldUpdateWithValidValue() {
    // Given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(10));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(false);
    publisher.setAttributes(publisherAttributes);

    // When
    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    // then
    assertEquals(10f, company.getSellerAttributes().getCreativeSuccessRateThreshold().floatValue());
  }

  @Test
  void shouldSuccessWhenSellerAttributeCreativeSuccessRateThresholdFieldUpdateWithNullValue() {
    // Given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setCreativeSuccessRateThreshold(null);
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(true);
    publisher.setAttributes(publisherAttributes);

    // When
    company = publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    // then
    assertNull(company.getSellerAttributes().getCreativeSuccessRateThreshold());
  }

  @Test
  void test_applySellerAttributesDynamicFloorEnabledWithValidValue() {
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setDynamicFloorEnabled(true);
    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getDynamicFloorEnabled(),
        company.getSellerAttributes().getDynamicFloorEnabled());
  }

  @Test
  void
      test_sellerAttributeExternalAdVerificationPolicyKeyFieldUpdateWithNull_andExternalAdVerificationEnabled() {
    PublisherDTO publisher = new PublisherDTO();
    publisher.setExternalAdVerificationEnabled(true);

    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.ZERO);

    publisher.setAttributes(publisherAttributes);
    Company company = new Company();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherAssembler.apply(NullableContext.nullableContext, company, publisher));

    assertEquals(
        ServerErrorCodes.SERVER_EXTERNAL_AD_VERIFICATION_POLICY_KEY_NOT_FOUND,
        exception.getErrorCode());
  }

  @Test
  void
      test_sellerAttributeExternalAdVerificationPolicyKeyFieldUpdateWithNull_andExternalAdVerificationNotEnabled() {
    PublisherDTO publisher = new PublisherDTO();
    publisher.setExternalAdVerificationEnabled(false);

    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.ZERO);
    publisherAttributes.setExternalAdVerificationPolicyKey(null);

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getExternalAdVerificationPolicyKey(),
        company.getSellerAttributes().getExternalAdVerificationPolicyKey());
  }

  @Test
  void
      test_sellerAttributeExternalAdVerificationPolicyKeyFieldUpdateWithValidValue_andExternalAdVerificationEnabled() {
    PublisherDTO publisher = new PublisherDTO();
    publisher.setExternalAdVerificationEnabled(true);

    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.ZERO);
    publisherAttributes.setExternalAdVerificationPolicyKey("a8a87c69-b518-4868-8e79-45c147356716");

    publisher.setAttributes(publisherAttributes);

    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    assertEquals(
        publisher.getAttributes().getExternalAdVerificationPolicyKey(),
        company.getSellerAttributes().getExternalAdVerificationPolicyKey());
  }

  @Test
  void shouldReturnPublisherDtoWithNullFieldsWhenModelIsNull() {
    // when
    var result = publisherAssembler.make(NullableContext.nullableContext, null, Set.of("pid"));

    // then
    assertNull(result.getPid());
  }

  @Test
  void shouldMapHumanSamplingRatesCorrectly() {
    // given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setHumanPrebidSampleRate(10);
    publisherAttributes.setHumanPostbidSampleRate(90);
    publisher.setAttributes(publisherAttributes);

    // when
    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    // then
    assertEquals(
        publisher.getAttributes().getHumanPrebidSampleRate(),
        company.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(
        publisher.getAttributes().getHumanPostbidSampleRate(),
        company.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void applySellerAttributesSetsCustomDealFloorEnabledFlag() {
    // given
    PublisherDTO publisher = new PublisherDTO();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setCustomDealFloorEnabled(true);
    publisher.setAttributes(publisherAttributes);

    // when
    Company company =
        publisherAssembler.apply(NullableContext.nullableContext, new Company(), publisher);

    // then
    assertEquals(
        publisher.getAttributes().isCustomDealFloorEnabled(),
        company.getSellerAttributes().isCustomDealFloorEnabled());
  }
}
