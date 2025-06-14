package com.nexage.app.dto;

import static com.nexage.app.util.validator.ValidationTestUtil.assertViolationsContains;
import static com.nexage.app.util.validator.ValidationTestUtil.stringOfLength;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BuyerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.VerificationType;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BidderConfigDTOValidationTest {

  private BidderConfigDTO bidderConfigDTO;
  private Validator validator;

  @BeforeEach
  void setup() {
    bidderConfigDTO = createValidBidderConfigDTO();
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  private static BidderConfigDTO createValidBidderConfigDTO() {
    var bidderConfigDTO = new BidderConfigDTO();
    bidderConfigDTO.setBidRequestCpm(new BigDecimal("1.5"));
    bidderConfigDTO.setCountryFilter("USA");
    bidderConfigDTO.setRequestRateFilter(5);
    bidderConfigDTO.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfigDTO.setSubscriptions(Set.of());
    bidderConfigDTO.setExchangeRegionals(Set.of());
    bidderConfigDTO.setAllowedDeviceTypes(Set.of());
    bidderConfigDTO.setVerificationType(VerificationType.STANDARD);
    bidderConfigDTO.setRegionLimits(Set.of());
    bidderConfigDTO.setDomainFilterAllowUnknownUrls(true);
    bidderConfigDTO.setAllowBridgeIdMatch(true);
    bidderConfigDTO.setAllowConnectId(true);
    bidderConfigDTO.setAllowIdGraphMatch(true);
    bidderConfigDTO.setAllowLiveramp(true);
    bidderConfigDTO.setDomainVerificationAuthLevel(BuyerDomainVerificationAuthLevel.ALLOW_ALL);
    bidderConfigDTO.setBidderConfigDenyAllowFilterLists(Set.of());
    bidderConfigDTO.setSendDealSizes(true);
    return bidderConfigDTO;
  }

  @Test
  void validBidderConfigDTO() {
    var violations = validator.validate(bidderConfigDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void bidRequestCpmNotNull() {
    bidderConfigDTO.setBidRequestCpm(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "bidRequestCpm", "must not be null");
  }

  @Test
  void bidRequestCpmMinZero() {
    bidderConfigDTO.setBidRequestCpm(new BigDecimal("-0.1"));
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "bidRequestCpm", "must be greater than or equal to 0");
  }

  @Test
  void countryFilterThreeLetterCode() {
    bidderConfigDTO.setCountryFilter("US");
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(
        violations,
        "countryFilter",
        "You must use a comma separated list of three letter country codes");
  }

  @Test
  void publishersFilterMaxSize10000() {
    bidderConfigDTO.setPublishersFilter(stringOfLength(10001));
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "publishersFilter", "size must be between 0 and 10000");
  }

  @Test
  void sitesFilterMaxSize1000() {
    bidderConfigDTO.setSitesFilter(stringOfLength(1001));
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "sitesFilter", "size must be between 0 and 1000");
  }

  @Test
  void requestRateFilterNotNull() {
    bidderConfigDTO.setRequestRateFilter(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "requestRateFilter", "must not be null");
  }

  @Test
  void requestRateFilterMinNegativeOne() {
    bidderConfigDTO.setRequestRateFilter(-2);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(
        violations, "requestRateFilter", "must be greater than or equal to -1");
  }

  @Test
  void formatTypeNotNull() {
    bidderConfigDTO.setFormatType(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "formatType", "must not be null");
  }

  @Test
  void exchangeRegionalsNotNull() {
    bidderConfigDTO.setExchangeRegionals(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "exchangeRegionals", "must not be null");
  }

  @Test
  void allowedDeviceTypesNotNull() {
    bidderConfigDTO.setAllowedDeviceTypes(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "allowedDeviceTypes", "must not be null");
  }

  @Test
  void allowedDeviceTypesValidated() {
    var invalidBidderDeviceType = new BidderDeviceTypeDTO();
    invalidBidderDeviceType.setDeviceTypeId(null);
    bidderConfigDTO.setAllowedDeviceTypes(Set.of(invalidBidderDeviceType));
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "allowedDeviceTypes[].deviceTypeId", "must not be null");
  }

  @Test
  void verificationTypeNotNull() {
    bidderConfigDTO.setVerificationType(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "verificationType", "must not be null");
  }

  @Test
  void regionLimitsNotNull() {
    bidderConfigDTO.setRegionLimits(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "regionLimits", "must not be null");
  }

  @Test
  void regionLimitsValidated() {
    var invalidRegionLimit = new BidderRegionLimitDTO();
    invalidRegionLimit.setName("");
    bidderConfigDTO.setRegionLimits(Set.of(invalidRegionLimit));
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "regionLimits[].name", "size must be between 1 and 255");
  }

  @Test
  void unknownNativeVersion() {
    bidderConfigDTO.setNativeVersion("1.0.0");
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "nativeVersion", "unknown native version");
  }

  @Test
  void domainFilterAllowUnknownUrlsNotNull() {
    bidderConfigDTO.setDomainFilterAllowUnknownUrls(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "domainFilterAllowUnknownUrls", "must not be null");
  }

  @Test
  void allowBridgeIdMatchNotNull() {
    bidderConfigDTO.setAllowBridgeIdMatch(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "allowBridgeIdMatch", "must not be null");
  }

  @Test
  void allowIdGraphMatchNotNull() {
    bidderConfigDTO.setAllowIdGraphMatch(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "allowIdGraphMatch", "must not be null");
  }

  @Test
  void allowLiverampNotNull() {
    bidderConfigDTO.setAllowLiveramp(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "allowLiveramp", "must not be null");
  }

  @Test
  void domainVerificationAuthLevelNotNull() {
    bidderConfigDTO.setDomainVerificationAuthLevel(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "domainVerificationAuthLevel", "must not be null");
  }

  @Test
  void bidderConfigDenyAllowFilterListNotNull() {
    bidderConfigDTO.setBidderConfigDenyAllowFilterLists(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "bidderConfigDenyAllowFilterLists", "must not be null");
  }

  @Test
  void sendDealSizesNotNull() {
    bidderConfigDTO.setSendDealSizes(null);
    var violations = validator.validate(bidderConfigDTO);
    assertViolationsContains(violations, "sendDealSizes", "must not be null");
  }
}
