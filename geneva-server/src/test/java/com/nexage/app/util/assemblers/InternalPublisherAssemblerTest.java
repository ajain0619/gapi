package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.context.CompanyContext;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.publisher.InternalPublisherAssembler;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalPublisherAssemblerTest {

  private static final long PID = 1L;
  private static final int VERSION = 2;
  private static final String ID = "id";
  private static final String NAME = "name";

  private static final Set<SellerEligibleBidders> SELLER_ELIGIBLE_BIDDERS = Collections.emptySet();
  private static final Set<PublisherEligibleBiddersDTO> PUBLISHER_ELIGIBLE_BIDDERS_DTOS =
      Collections.emptySet();
  private static final String CURRENCY = "currency";
  private static final long CONTACT_USER_PID = 4L;
  private static final boolean AD_SERVING_ENABLED = true;
  private static final boolean RESTRICT_DRILL_DOWN = true;
  private static final double HOUSE_AD_SERVING_FEE = 5.5;
  private static final double NON_REMNANT_HOUSE_AD_CAP = 6.6;
  private static final double HOUSE_AD_OVERAGE_FEE = 7.7;
  private static final boolean CPI_TRACKING_ENABLED = true;
  private static final String CPI_CONVERSION_NOTICE_URL = "cpi_conversion_notice_url";
  private static final boolean RTB_ENABLED = true;
  private static final boolean MEDIATION_ENABLED = true;
  private static final boolean RTB_REVENUE_REPORT_ENABLED = true;
  private static final String SALESFORCE_ID = "salesforce_id";
  private static final BigDecimal BIDDER_AD_SERVING_FEE = new BigDecimal("8.8");
  private static final Status STATUS = Status.ACTIVE;
  private static final String GLOBAL_ALIAS_NAME = "global_alias_name";
  private static final String WEBSITE = "website";
  private static final String DESCRIPTION = "description";
  private static final CompanyType COMPANY_TYPE = CompanyType.NEXAGE;
  private static final boolean TEST = true;
  private static final boolean SELF_SERVE_ALLOWED = true;
  private static final long REGION_ID = 9L;
  private static final boolean PAYOUT_ENABLED = true;
  private static final int NUMBER_OF_RTB_TAGS = 10;
  private static final int NUMBER_OF_MEDIATION_SITES = 11;
  private static final Set<String> AD_SOURCE_NAMES = Collections.emptySet();
  private static final Set<String> EXTERNAL_DATA_PROVIDER_NAMES = Collections.emptySet();
  private static final int ACTIVE_IOS = 12;
  private static final BigDecimal CREDIT = new BigDecimal("13.13");
  private static final int NUMBER_OF_USERS = 14;
  private static final boolean HAS_HEADER_BIDDING_SITES = true;
  private static final double DIRECT_AD_SERVING_FEE = 15.15;
  private static final boolean DEFAULT_RTB_PROFILES_ENABLED = true;
  private static final long SELLER_SEAT_PID = 16L;
  private static final Boolean EXTERNAL_AD_VERIFICATION_ENABLED = true;
  private static final Company.EstimateTimeRemaining ESTIMATE_TIME_REMAINING =
      new Company.EstimateTimeRemaining(Company.EstimateTimeRemaining.ETR.VALID, BigInteger.ONE);
  private static final boolean THIRD_PARTY_FRAUD_DETECTION_ENABLED = true;
  private static final boolean FRAUD_DETECTION_JAVASCRIPT_ENABLED = true;

  @InjectMocks InternalPublisherAssembler internalPublisherAssembler;

  @Mock Company companyMock;
  @Mock InternalPublisherAttributesAssembler internalPublisherAttributesAssemblerMock;
  @Mock PublisherEligibleBiddersAssembler publisherEligibleBiddersAssemblerMock;
  @Mock UserContext userContextMock;
  @Mock PublisherDTO publisherDTOMock;
  @Mock SellerAttributes sellerAttributesMock;
  @Mock PublisherAttributes publisherAttributesMock;

  @Test
  void make_makesCorrectDTO() {
    when(companyMock.getPid()).thenReturn(PID);
    when(companyMock.getVersion()).thenReturn(VERSION);
    when(companyMock.getId()).thenReturn(ID);
    when(companyMock.getName()).thenReturn(NAME);
    when(companyMock.getVersion()).thenReturn(VERSION);
    when(companyMock.getSellerAttributes()).thenReturn(sellerAttributesMock);
    when(companyMock.getEligibleBidders()).thenReturn(SELLER_ELIGIBLE_BIDDERS);
    when(companyMock.getCurrency()).thenReturn(CURRENCY);
    when(companyMock.getContactUserPid()).thenReturn(CONTACT_USER_PID);
    when(companyMock.isAdServingEnabled()).thenReturn(AD_SERVING_ENABLED);
    when(companyMock.isRestrictDrillDown()).thenReturn(RESTRICT_DRILL_DOWN);
    when(companyMock.getHouseAdServingFee()).thenReturn(HOUSE_AD_SERVING_FEE);
    when(companyMock.getNonRemnantHouseAdCap()).thenReturn(NON_REMNANT_HOUSE_AD_CAP);
    when(companyMock.getHouseAdOverageFee()).thenReturn(HOUSE_AD_OVERAGE_FEE);
    when(companyMock.isCpiTrackingEnabled()).thenReturn(CPI_TRACKING_ENABLED);
    when(companyMock.getCpiConversionNoticeUrl()).thenReturn(CPI_CONVERSION_NOTICE_URL);
    when(companyMock.isRtbEnabled()).thenReturn(RTB_ENABLED);
    when(companyMock.isMediationEnabled()).thenReturn(MEDIATION_ENABLED);
    when(companyMock.isRtbRevenueReportEnabled()).thenReturn(RTB_REVENUE_REPORT_ENABLED);
    when(companyMock.getSalesforceId()).thenReturn(SALESFORCE_ID);
    when(companyMock.getBidderAdServingFee()).thenReturn(BIDDER_AD_SERVING_FEE);
    when(companyMock.getStatus()).thenReturn(STATUS);
    when(companyMock.getGlobalAliasName()).thenReturn(GLOBAL_ALIAS_NAME);
    when(companyMock.getWebsite()).thenReturn(WEBSITE);
    when(companyMock.getDescription()).thenReturn(DESCRIPTION);
    when(companyMock.getType()).thenReturn(COMPANY_TYPE);
    when(companyMock.getTest()).thenReturn(TEST);
    when(companyMock.isSelfServeAllowed()).thenReturn(SELF_SERVE_ALLOWED);
    when(companyMock.getRegionId()).thenReturn(REGION_ID);
    when(companyMock.getPayoutEnabled()).thenReturn(PAYOUT_ENABLED);
    when(companyMock.getNumberOfRtbTags()).thenReturn(NUMBER_OF_RTB_TAGS);
    when(companyMock.getNumberOfMediationSites()).thenReturn(NUMBER_OF_MEDIATION_SITES);
    when(companyMock.getAdsourceNames()).thenReturn(AD_SOURCE_NAMES);
    when(companyMock.getExternalDataProviderNames()).thenReturn(EXTERNAL_DATA_PROVIDER_NAMES);
    when(companyMock.getActiveIOs()).thenReturn(ACTIVE_IOS);
    when(companyMock.getCredit()).thenReturn(CREDIT);
    when(companyMock.getNumberOfUsers()).thenReturn(NUMBER_OF_USERS);
    when(companyMock.getHasHeaderBiddingSites()).thenReturn(HAS_HEADER_BIDDING_SITES);
    when(companyMock.getDirectAdServingFee()).thenReturn(DIRECT_AD_SERVING_FEE);
    when(companyMock.isDefaultRtbProfilesEnabled()).thenReturn(DEFAULT_RTB_PROFILES_ENABLED);
    when(companyMock.getSellerSeatPid()).thenReturn(SELLER_SEAT_PID);
    when(companyMock.getExternalAdVerificationEnabled())
        .thenReturn(EXTERNAL_AD_VERIFICATION_ENABLED);
    when(internalPublisherAttributesAssemblerMock.make(
            any(NullableContext.class), eq(sellerAttributesMock)))
        .thenReturn(publisherAttributesMock);
    when(publisherEligibleBiddersAssemblerMock.make(
            any(CompanyContext.class), eq(SELLER_ELIGIBLE_BIDDERS)))
        .thenReturn(PUBLISHER_ELIGIBLE_BIDDERS_DTOS);
    when(companyMock.getThirdPartyFraudDetectionEnabled())
        .thenReturn(THIRD_PARTY_FRAUD_DETECTION_ENABLED);
    when(companyMock.getFraudDetectionJavascriptEnabled())
        .thenReturn(FRAUD_DETECTION_JAVASCRIPT_ENABLED);

    PublisherDTO publisherDTO = internalPublisherAssembler.make(null, companyMock);

    assertEquals(PID, publisherDTO.getPid());
    assertEquals(VERSION, publisherDTO.getVersion());
    assertEquals(PID, publisherDTO.getPid());
    assertEquals(NAME, publisherDTO.getName());
    assertEquals(publisherAttributesMock, publisherDTO.getAttributes());
    assertEquals(PUBLISHER_ELIGIBLE_BIDDERS_DTOS, publisherDTO.getEligibleBidderGroups());
    assertEquals(CURRENCY, publisherDTO.getCurrency());
    assertNotNull(publisherDTO.getHbPartnerAttributes());
    assertEquals(CONTACT_USER_PID, publisherDTO.getContactUserPid());
    assertEquals(AD_SERVING_ENABLED, publisherDTO.getAdServingEnabled());
    assertEquals(RESTRICT_DRILL_DOWN, publisherDTO.getRestrictDrillDown());
    assertEquals(HOUSE_AD_SERVING_FEE, publisherDTO.getHouseAdServingFee());
    assertEquals(NON_REMNANT_HOUSE_AD_CAP, publisherDTO.getNonRemnantHouseAdCap());
    assertEquals(HOUSE_AD_OVERAGE_FEE, publisherDTO.getHouseAdOverageFee());
    assertEquals(CPI_TRACKING_ENABLED, publisherDTO.getCpiTrackingEnabled());
    assertEquals(CPI_CONVERSION_NOTICE_URL, publisherDTO.getCpiConversionNoticeUrl());
    assertEquals(RTB_ENABLED, publisherDTO.getRtbEnabled());
    assertEquals(MEDIATION_ENABLED, publisherDTO.getMediationEnabled());
    assertEquals(RTB_REVENUE_REPORT_ENABLED, publisherDTO.getRtbRevenueReportEnabled());
    assertEquals(SALESFORCE_ID, publisherDTO.getSalesforceId());
    assertEquals(BIDDER_AD_SERVING_FEE, publisherDTO.getBidderAdServingFee());
    assertEquals(STATUS, publisherDTO.getStatus());
    assertEquals(GLOBAL_ALIAS_NAME, publisherDTO.getGlobalAliasName());
    assertEquals(WEBSITE, publisherDTO.getWebsite());
    assertEquals(DESCRIPTION, publisherDTO.getDescription());
    assertEquals(COMPANY_TYPE, publisherDTO.getType());
    assertEquals(TEST, publisherDTO.getTest());
    assertEquals(SELF_SERVE_ALLOWED, publisherDTO.getSelfServeAllowed());
    assertEquals(REGION_ID, publisherDTO.getRegionId());
    assertEquals(PAYOUT_ENABLED, publisherDTO.getPayoutEnabled());
    assertEquals(NUMBER_OF_RTB_TAGS, publisherDTO.getNumberOfRtbTags());
    assertEquals(NUMBER_OF_MEDIATION_SITES, publisherDTO.getNumberOfMediationSites());
    assertEquals(AD_SOURCE_NAMES, publisherDTO.getAdsourceNames());
    assertEquals(EXTERNAL_DATA_PROVIDER_NAMES, publisherDTO.getExternalDataProviderNames());
    assertEquals(ACTIVE_IOS, publisherDTO.getActiveIOs());
    assertEquals(CREDIT, publisherDTO.getCredit());
    assertEquals(NUMBER_OF_USERS, publisherDTO.getNumberOfUsers());
    assertEquals(HAS_HEADER_BIDDING_SITES, publisherDTO.getHasHeaderBiddingSites());
    assertEquals(DIRECT_AD_SERVING_FEE, publisherDTO.getDirectAdServingFee());
    assertEquals(DEFAULT_RTB_PROFILES_ENABLED, publisherDTO.getDefaultRtbProfilesEnabled());
    assertEquals(SELLER_SEAT_PID, publisherDTO.getSellerSeatPid());
    assertEquals(EXTERNAL_AD_VERIFICATION_ENABLED, publisherDTO.getExternalAdVerificationEnabled());
    assertEquals(
        THIRD_PARTY_FRAUD_DETECTION_ENABLED, publisherDTO.getThirdPartyFraudDetectionEnabled());
    assertEquals(
        FRAUD_DETECTION_JAVASCRIPT_ENABLED, publisherDTO.getFraudDetectionJavascriptEnabled());
  }

  @Test
  void apply_returnsCorrectCompany() {
    when(userContextMock.isNexageAdminOrManager()).thenReturn(true);
    when(publisherDTOMock.getName()).thenReturn(NAME);
    when(publisherDTOMock.getContactUserPid()).thenReturn(CONTACT_USER_PID);
    when(publisherDTOMock.getAdServingEnabled()).thenReturn(AD_SERVING_ENABLED);
    when(publisherDTOMock.getRestrictDrillDown()).thenReturn(RESTRICT_DRILL_DOWN);
    when(publisherDTOMock.getDirectAdServingFee()).thenReturn(DIRECT_AD_SERVING_FEE);
    when(publisherDTOMock.getHouseAdServingFee()).thenReturn(HOUSE_AD_SERVING_FEE);
    when(publisherDTOMock.getNonRemnantHouseAdCap()).thenReturn(NON_REMNANT_HOUSE_AD_CAP);
    when(publisherDTOMock.getHouseAdOverageFee()).thenReturn(HOUSE_AD_OVERAGE_FEE);
    when(publisherDTOMock.getCpiTrackingEnabled()).thenReturn(CPI_TRACKING_ENABLED);
    when(publisherDTOMock.getCpiConversionNoticeUrl()).thenReturn(CPI_CONVERSION_NOTICE_URL);
    when(publisherDTOMock.getRtbEnabled()).thenReturn(RTB_ENABLED);
    when(publisherDTOMock.getMediationEnabled()).thenReturn(MEDIATION_ENABLED);
    when(publisherDTOMock.getRtbRevenueReportEnabled()).thenReturn(RTB_REVENUE_REPORT_ENABLED);
    when(publisherDTOMock.getSalesforceId()).thenReturn(SALESFORCE_ID);
    when(publisherDTOMock.getBidderAdServingFee()).thenReturn(BIDDER_AD_SERVING_FEE);
    when(publisherDTOMock.getStatus()).thenReturn(STATUS);
    when(publisherDTOMock.getGlobalAliasName()).thenReturn(GLOBAL_ALIAS_NAME);
    when(publisherDTOMock.getWebsite()).thenReturn(WEBSITE);
    when(publisherDTOMock.getDescription()).thenReturn(DESCRIPTION);
    when(publisherDTOMock.getType()).thenReturn(COMPANY_TYPE);
    when(publisherDTOMock.getTest()).thenReturn(TEST);
    when(publisherDTOMock.getSelfServeAllowed()).thenReturn(SELF_SERVE_ALLOWED);
    when(publisherDTOMock.getRegionId()).thenReturn(REGION_ID);
    when(publisherDTOMock.getPayoutEnabled()).thenReturn(PAYOUT_ENABLED);
    when(publisherDTOMock.getEstimatedTimeRemain()).thenReturn(ESTIMATE_TIME_REMAINING);
    when(publisherDTOMock.getDefaultRtbProfilesEnabled()).thenReturn(DEFAULT_RTB_PROFILES_ENABLED);
    when(publisherDTOMock.getCurrency()).thenReturn(CURRENCY);
    when(publisherDTOMock.getSellerSeatPid()).thenReturn(SELLER_SEAT_PID);
    when(publisherDTOMock.getExternalAdVerificationEnabled())
        .thenReturn(EXTERNAL_AD_VERIFICATION_ENABLED);
    when(publisherDTOMock.getThirdPartyFraudDetectionEnabled())
        .thenReturn(THIRD_PARTY_FRAUD_DETECTION_ENABLED);
    when(publisherDTOMock.getFraudDetectionJavascriptEnabled())
        .thenReturn(FRAUD_DETECTION_JAVASCRIPT_ENABLED);

    Company company = internalPublisherAssembler.apply(null, companyMock, publisherDTOMock);

    assertEquals(companyMock, company);
    verify(companyMock, times(1)).setName(NAME);
    verify(companyMock, times(1)).setContactUserPid(CONTACT_USER_PID);
    verify(companyMock, times(1)).setAdServingEnabled(AD_SERVING_ENABLED);
    verify(companyMock, times(1)).setRestrictDrillDown(RESTRICT_DRILL_DOWN);
    verify(companyMock, times(1)).setDirectAdServingFee(DIRECT_AD_SERVING_FEE);
    verify(companyMock, times(1)).setHouseAdServingFee(HOUSE_AD_SERVING_FEE);
    verify(companyMock, times(1)).setNonRemnantHouseAdCap(NON_REMNANT_HOUSE_AD_CAP);
    verify(companyMock, times(1)).setHouseAdOverageFee(HOUSE_AD_OVERAGE_FEE);
    verify(companyMock, times(1)).setCpiTrackingEnabled(CPI_TRACKING_ENABLED);
    verify(companyMock, times(1)).setCpiConversionNoticeUrl(CPI_CONVERSION_NOTICE_URL);
    verify(companyMock, times(1)).setRtbEnabled(RTB_ENABLED);
    verify(companyMock, times(1)).setMediationEnabled(MEDIATION_ENABLED);
    verify(companyMock, times(1)).setRtbRevenueReportEnabled(RTB_REVENUE_REPORT_ENABLED);
    verify(companyMock, times(1)).setSalesforceId(SALESFORCE_ID);
    verify(companyMock, times(1)).setBidderAdServingFee(BIDDER_AD_SERVING_FEE);
    verify(companyMock, times(1)).setStatus(STATUS);
    verify(companyMock, times(1)).setGlobalAliasName(GLOBAL_ALIAS_NAME);
    verify(companyMock, times(1)).setWebsite(WEBSITE);
    verify(companyMock, times(1)).setDescription(DESCRIPTION);
    verify(companyMock, times(1)).setType(COMPANY_TYPE);
    verify(companyMock, times(1)).setTest(TEST);
    verify(companyMock, times(1)).setSelfServeAllowed(SELF_SERVE_ALLOWED);
    verify(companyMock, times(1)).setRegionId(REGION_ID);
    verify(companyMock, times(1)).setPayoutEnabled(PAYOUT_ENABLED);
    verify(companyMock, times(1)).setEstimatedTimeRemaining(ESTIMATE_TIME_REMAINING);
    verify(companyMock, times(1)).setDefaultRtbProfilesEnabled(DEFAULT_RTB_PROFILES_ENABLED);
    verify(companyMock, times(1)).setCurrency(CURRENCY);
    verify(companyMock, times(1)).setSellerSeatPid(SELLER_SEAT_PID);
    verify(companyMock, times(1))
        .setExternalAdVerificationEnabled(EXTERNAL_AD_VERIFICATION_ENABLED);
    verify(companyMock, times(1))
        .setThirdPartyFraudDetectionEnabled(THIRD_PARTY_FRAUD_DETECTION_ENABLED);
    verify(companyMock, times(1))
        .setFraudDetectionJavascriptEnabled(FRAUD_DETECTION_JAVASCRIPT_ENABLED);
  }

  @Test
  void apply_externalAdVerificationEnabledIsNull_returnsCorrectCompany() {
    when(userContextMock.isNexageAdminOrManager()).thenReturn(true);
    when(publisherDTOMock.getExternalAdVerificationEnabled()).thenReturn(null);

    Company company = internalPublisherAssembler.apply(null, companyMock, publisherDTOMock);

    assertEquals(companyMock, company);
    verify(companyMock, times(0)).setExternalAdVerificationEnabled(any());
  }

  @Test
  void apply_thirdPartyFraudDetectionEnabledIsNull_returnsCorrectCompany() {
    when(userContextMock.isNexageAdminOrManager()).thenReturn(true);
    when(publisherDTOMock.getThirdPartyFraudDetectionEnabled()).thenReturn(null);
    when(publisherDTOMock.getPid()).thenReturn(null);

    Company company = internalPublisherAssembler.apply(null, companyMock, publisherDTOMock);

    assertEquals(companyMock, company);
    verify(companyMock, times(2)).setThirdPartyFraudDetectionEnabled(any());
  }

  @Test
  void apply_fraudDetectionJavascriptEnabledIsNull_returnsCorrectCompany() {
    when(userContextMock.isNexageAdminOrManager()).thenReturn(true);
    when(publisherDTOMock.getFraudDetectionJavascriptEnabled()).thenReturn(null);

    Company company = internalPublisherAssembler.apply(null, companyMock, publisherDTOMock);

    assertEquals(companyMock, company);
    verify(companyMock, times(0)).setFraudDetectionJavascriptEnabled(any());
  }
}
