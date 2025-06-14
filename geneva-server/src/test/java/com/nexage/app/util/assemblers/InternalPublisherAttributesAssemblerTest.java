package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SellerType;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalPublisherAttributesAssemblerTest {

  @Mock private UserContext userContext;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;

  @InjectMocks private InternalPublisherAttributesAssembler internalPublisherAttributesAssembler;

  @BeforeEach
  public void setup() {
    when(userContext.isNexageAdminOrManager()).thenReturn(true);
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SellerAttributes.class), any(PublisherAttributes.class)))
        .thenReturn(false);
  }

  @Test
  void shouldUpdateModelWhenApplyingDTOChanges() {
    PublisherAttributes dto =
        PublisherAttributes.newBuilder()
            .withAdFeedbackOptOut(true)
            .withSellerType(SellerType.DIRECT)
            .withBuyerTransparencyOptOut(true)
            .withRevenueGroupPid(1L)
            .withSellerDomainVerificationAuthLevel(
                SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED)
            .withHumanOptOut(true)
            .withSmartQPSEnabled(true)
            .withAdStrictApproval(true)
            .withEnableCtvSelling(true)
            .withRawResponse(true)
            .build();

    SellerAttributes model = new SellerAttributes();
    assertNull(model.getAdFeedbackOptOut(), "adFeedbackOptOut is not set");
    assertNull(model.getSellerType(), "sellerType is not set");
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED,
        model.getSellerDomainVerificationAuthLevel(),
        "sellerDomainVerificationAutthLevel is not set - default to ALLOW_BASED_ON_BIDDER");
    assertFalse(model.getHumanOptOut(), "humanOptOut is not set and it is false");
    assertFalse(model.getSmartQPSEnabled(), "smartQPSEnabled is not set and it is false");
    assertFalse(model.isRawResponse());

    internalPublisherAttributesAssembler.apply(null, model, dto);
    assertTrue(model.getAdFeedbackOptOut(), "adFeedbackOptOut is set");
    assertTrue(model.getHumanOptOut(), "humanOptOut is set");
    assertTrue(model.getSmartQPSEnabled(), "smartQPSEnabled is set");
    assertEquals(SellerType.DIRECT, model.getSellerType(), "sellerType is set");
    assertTrue(model.getBuyerTransparencyOptOut(), "buyerTransparencyOptOut is set");
    assertEquals(1L, model.getRevenueGroupPid().longValue());
    assertTrue(model.isRawResponse());

    when(userContext.isNexageAdminOrManager()).thenReturn(false);
    model.setAdFeedbackOptOut(false);
    model.setHumanOptOut(false);
    model.setSmartQPSEnabled(false);
    model.setRawResponse(false);

    internalPublisherAttributesAssembler.apply(null, model, dto);
    assertFalse(model.getAdFeedbackOptOut(), "adFeedbackOptOut is not set");
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED,
        model.getSellerDomainVerificationAuthLevel());
    assertFalse(model.getHumanOptOut(), "humanOptOut is not set");
    assertFalse(model.getSmartQPSEnabled(), "smartQPSEnabled is not set");
    assertTrue(model.getAdStrictApproval());
    assertTrue(model.isEnableCtvSelling());
    assertFalse(model.isRawResponse());
  }

  @Test
  void shouldNotUpdateOptionalModelFieldsWhenOptionalDtoFieldsMissing() {
    PublisherAttributes dto =
        PublisherAttributes.newBuilder()
            .withSellerDomainVerificationAuthLevel(null)
            .withSmartQPSEnabled(null)
            .withAdStrictApproval(null)
            .withRawResponse(null)
            .build();

    SellerAttributes model = new SellerAttributes();

    internalPublisherAttributesAssembler.apply(null, model, dto);

    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED,
        model.getSellerDomainVerificationAuthLevel());
    assertFalse(model.getSmartQPSEnabled());
    assertFalse(model.getAdStrictApproval());
    assertFalse(model.isRawResponse());
  }

  @Test
  void shouldCreateNewSmartExchangeAttributesWhenDTOPresentAndUserHasPermissions() {
    when(userContext.canEditSmartExchange()).thenReturn(true);
    PublisherAttributes publisherAttributes = createPublisherAttributes();

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(
            null, new SellerAttributes(), publisherAttributes);

    SmartExchangeAttributes smartExchangeAttributes = sellerAttributes.getSmartExchangeAttributes();
    assertNotNull(smartExchangeAttributes);
    assertNull(smartExchangeAttributes.getPid());
    assertNull(smartExchangeAttributes.getVersion());
    validateSmartExchangeAttributes(smartExchangeAttributes, sellerAttributes, true);
  }

  @Test
  void shouldThrowExceptionWhenCreatingSmartExchangeAttributesAndPermissionsMissing() {
    PublisherAttributes publisherAttributes = createPublisherAttributes();
    SellerAttributes sellerAttributes = new SellerAttributes();

    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () ->
                internalPublisherAttributesAssembler.apply(
                    null, sellerAttributes, publisherAttributes));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldCreateSmartExchangeAttributesWhenDTOSmartMarginDisabledAndPermissionsMissing() {
    PublisherAttributes publisherAttributes = createPublisherAttributes();
    publisherAttributes.getSmartExchangeAttributes().setSmartMarginEnabled(false);

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(
            null, new SellerAttributes(), publisherAttributes);

    SmartExchangeAttributes smartExchangeAttributes = sellerAttributes.getSmartExchangeAttributes();
    assertNotNull(smartExchangeAttributes);
    assertNull(smartExchangeAttributes.getPid());
    assertNull(smartExchangeAttributes.getVersion());
    validateSmartExchangeAttributes(smartExchangeAttributes, sellerAttributes, false);
  }

  @Test
  void shouldNotCreateSmartExchangeAttributesWhenDTONull() {
    PublisherAttributes publisherAttributes = PublisherAttributes.newBuilder().build();

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(
            null, new SellerAttributes(), publisherAttributes);

    assertNull(sellerAttributes.getSmartExchangeAttributes());
  }

  @Test
  void shouldUpdateSmartExchangeAttributesWhenDTOPresentAndUserHasPermissions() {
    SellerAttributes model = new SellerAttributes();

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = createPublisherAttributes();
    when(userContext.canEditSmartExchange()).thenReturn(true);

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(null, model, publisherAttributes);

    SmartExchangeAttributes updated = sellerAttributes.getSmartExchangeAttributes();
    assertNotNull(updated);
    assertEquals(1L, updated.getPid());
    assertEquals(1, updated.getVersion());
    validateSmartExchangeAttributes(updated, sellerAttributes, true);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingSmartExchangeAttributesAndPermissionsMissing() {
    SellerAttributes model = new SellerAttributes();

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = createPublisherAttributes();

    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> internalPublisherAttributesAssembler.apply(null, model, publisherAttributes));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldNotUpdateWhenSameSmartExchangeAttributesPresentAndPermissionsMissing() {
    SellerAttributes model = new SellerAttributes();
    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(true);
    smartExchangeAttributes.setSellerAttributes(model);
    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = createPublisherAttributes();

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(null, model, publisherAttributes);

    SmartExchangeAttributes updated = sellerAttributes.getSmartExchangeAttributes();
    assertNotNull(updated);
    assertEquals(1L, updated.getPid());
    assertEquals(1, updated.getVersion());
    validateSmartExchangeAttributes(updated, sellerAttributes, true);
  }

  @Test
  void shouldNotUpdateSmartExchangeAttributesWhenDTONull() {
    SellerAttributes model = new SellerAttributes();
    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);
    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = PublisherAttributes.newBuilder().build();

    SellerAttributes sellerAttributes =
        internalPublisherAttributesAssembler.apply(null, model, publisherAttributes);

    SmartExchangeAttributes updated = sellerAttributes.getSmartExchangeAttributes();
    assertNotNull(updated);
    assertEquals(1L, updated.getPid());
    assertEquals(1, updated.getVersion());
    validateSmartExchangeAttributes(updated, sellerAttributes, false);
  }

  private PublisherAttributes createPublisherAttributes() {
    SmartExchangeAttributesDTO smartExchangeAttributesDTO =
        SmartExchangeAttributesDTO.newBuilder().withSmartMarginEnabled(true).build();

    return PublisherAttributes.newBuilder()
        .withSmartExchangeAttributes(smartExchangeAttributesDTO)
        .build();
  }

  @Test
  void shouldThrowWhenUserInputsCreativeSuccessRateThresholdInvalidUpperLimitData() {
    // Given
    SellerAttributes model = new SellerAttributes();

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = createPublisherAttributes();
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(101));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> internalPublisherAttributesAssembler.apply(null, model, publisherAttributes));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenUserInputsCreativeSuccessRateThresholdInvalidLowerLimitData() {
    // Given
    SellerAttributes model = new SellerAttributes();

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);
    PublisherAttributes publisherAttributes = createPublisherAttributes();
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(0));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> internalPublisherAttributesAssembler.apply(null, model, publisherAttributes));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenUserInputsCreativeSuccessRateThresholdValidDataAndOptOutTrue() {
    // Given
    SellerAttributes model = new SellerAttributes();

    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setPid(1L);
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSmartMarginOverride(false);
    smartExchangeAttributes.setSellerAttributes(model);

    model.setSmartExchangeAttributes(smartExchangeAttributes);

    PublisherAttributes publisherAttributes = createPublisherAttributes();

    // when  creativesuccessratethreshold has valid value and  optout is true
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(10));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> internalPublisherAttributesAssembler.apply(null, model, publisherAttributes));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_THRESHOLD_NOT_ALLOWED,
        exception.getErrorCode());
  }

  @Test
  void shouldSuccessWhenUserInputsCreativeSuccessRateThresholdValidData() {
    // Given
    SellerAttributes model = new SellerAttributes();
    PublisherAttributes publisherAttributes = createPublisherAttributes();
    publisherAttributes.setSmartExchangeAttributes(null);
    publisherAttributes.setCreativeSuccessRateThreshold(new BigDecimal(10));
    publisherAttributes.setCreativeSuccessRateThresholdOptOut(false);

    // when
    internalPublisherAttributesAssembler.apply(null, model, publisherAttributes);

    // then
    assertEquals(10f, model.getCreativeSuccessRateThreshold().floatValue());
  }

  private void validateSmartExchangeAttributes(
      SmartExchangeAttributes smartExchangeAttributes,
      SellerAttributes sellerAttributes,
      boolean smartMarginEnabled) {

    assertEquals(smartMarginEnabled, smartExchangeAttributes.getSmartMarginOverride());
    assertEquals(sellerAttributes, smartExchangeAttributes.getSellerAttributes());
  }
}
