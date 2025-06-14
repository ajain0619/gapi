package com.nexage.geneva.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {

  public static final String SITE_PID = "{sitePid}";
  public static final String SITE_DCN = "{siteDcn}";
  public static final String PUBLISHER_PID = "{publisherPid}";
  public static final String POSITION_PID = "{positionPid}";
  public static final String POSITION_ID = "{positionId}";
  public static final String PLACEMENT_NAME = "{placementName}";
  public static final String TIER_PID = "{tierPid}";
  public static final String START = "{start}";
  public static final String STOP = "{stop}";
  public static final String INTERVAL = "{interval}";
  public static final String BUYER_PID = "{buyerPid}";
  public static final String BIDDER_CONFIG_PID = "{bidderConfigPid}";
  public static final String ADSOURCE_PID = "{adsourcePid}";
  public static final String ADVERTISER_PID = "{advertiserPid}";
  public static final String SELLER_PID = "{sellerPid}";
  public static final String TARGET_TYPE = "{targetType}";
  public static final String PREFIX = "{prefix}";
  public static final String TAG_PID = "{tagPid}";
  public static final String TAG_VALUES_PID = "{tagValuesPid}";
  public static final String CATEGORY_PID = "{categoryPid}";
  public static final String TAG_MAPPINGS_PID = "{tagMappingsPid}";
  public static final String RTBPROFILE_GROUP_PID = "{rtbprofilegroupPid}";
  public static final String PROFILE_LIBRARY_PID = "{libraryPid}";
  public static final String DEAL_PID = "{dealPid}";
  public static final String STATUS = "{status}";
  public static final String CAMPAIGN_ID = "{campaignId}";
  public static final String USER_PID = "{userPid}";
  public static final String COMPANY_PID = "{companyPid}";
  public static final String OLD_PASSWORD = "{oldPassword}";
  public static final String NEW_PASSWORD = "{newPassword}";
  public static final String HANDSHAKE_PID = "{handshakePid}";
  public static final String HANDSHAKE_PARAMS = "{handshakeParams}";
  public static final String REPORT_CATEGORY = "{reportCategory}";
  public static final String REPORT_METADATA_ID = "{reportMetadataId}";
  public static final String REPORT_DRILLDOWN = "{reportDrillDown}";
  public static final String REPORT_FILTER = "{reportFilter}";
  public static final String REPORT_COMPANY_FILTER = "{reportCompanyFilter}";
  public static final String USERNAME = "{username}";
  public static final String PASSWORD = "{password}";
  public static final String COMPANY_ID = "{companyId}";
  public static final String REPORT_ID = "{reportId}";
  public static final String TX_ID = "{txId}";
  public static final String REVISION_NUMBER = "{revision}";
  public static final String TARGET_SITE = "{targetSite}";
  public static final String TARGET_POSITION = "{targetPosition}";
  public static final String PROFILE_PID = "{profilePID}";
  public static final String PRIMARY_ID = "{PrimaryId}";
  public static final String PRIMARY_NAME = "{PrimaryName}";
  public static final String SECONDARY_ID = "{SecondaryId}";
  public static final String SECONDARY_NAME = "{SecondaryName}";
  public static final String BIDDER_PID = "{bidderPID}";
  public static final String CREATIVE_ID = "{creativeID}";
  public static final String DETAIL = "{detail}";
  public static final String RULE_PID = "{rulePid}";
  public static final String RULE_PIDS = "{pids}";
  public static final String RTB_PID = "{rtbPid}";
  public static final String BUYER_GROUP_PID = "{buyerGroupPid}";
  public static final String BUYER_SEAT_PID = "{seatPid}";
  public static final String RULE_TYPE = "{ruleType}";
  public static final String SIZE = "{size}";
  public static final String PAGE = "{page}";
  public static final String QT = "{qt}";
  public static final String QF = "{qf}";
  public static final String QO = "{qo}";
  public static final String SITE_TYPE = "{siteType}";
  public static final String PLACEMENT_TYPE = "{placementType}";
  public static final String ATTRIBUTE_PID = "{attributePid}";
  public static final String ATTRIBUTE_VALUE_PID = "{attributeValuePid}";
  public static final String HBPARTNER_PID = "{hbPartnerPid}";
  public static final String ACTION_TYPE = "{actionType}";
  public static final String SELLERSEAT_PID = "{sellerSeatPid}";
  public static final String SELLERSEATRULE_PID = "{sellerSeatRulePid}";
  public static final String KEY_VALUE_PAIR_PID = "{keyValuePairPid}";
  public static final String PLACEMENT_ID = "{placementId}";
  public static final String COMPOUND_SEGMENT_ID = "{compoundSegmentId}";
  public static final String START_DATE = "{startDate}";
  public static final String STOP_DATE = "{stopDate}";
  public static final String PLACEMENT_MEMO = "{placementMemo}";
  public static final String NAME = "{name}";
  public static final String PIDS = "{pids}";
  public static final String FEE_ADJUSTMENT_PID = "{feeAdjustmentPid}";
  public static final String FEE_ADJUSTMENT_ENABLED = "{feeAdjustmentEnabled}";
  public static final String EXPERIMENT_PID = "{experimentPid}";
  public static final String MIMIMAL = "{minimal}";
  public static final String VERIFICATION_LIST_PID = "{listPid}";
  public static final String APP_VERIFICATION_ID = "{appVerificationId}";
  public static final String DOMAIN_VERIFICATION_ID = "{domainVerificationId}";
  public static final String APP_ALIAS_VERIFICATION_ID = "{appAliasVerificationId}";
  public static final String POST_AUCTION_DISCOUNT_PID = "{postAuctionDiscountPid}";
  public static final String POST_AUCTION_DISCOUNT_ENABLED = "{postAuctionDiscountEnabled}";
  public static final String FILTER_LIST_ID = "{filterListId}";
  public static final String FILTER_LIST_TYPE = "{filterListType}";
  public static final String SEGMENT_IDS = "{segmentIds}";
  public static final String FILE_PID = "{filePid}";
  public static final String AD_NOTIFICATION_PID = "{adNotificationPid}";
  public static final String SORT = "{sort}";
  public static final String FETCH = "{fetch}";
  public static final String COMPANY_TYPE = "{companyType}";
  public static final String FORMAT = "{format}";
  public static final String BIDDER_ID = "{bidder_id}";
  public static final String AUCTION_RUN_HASH_ID = "{auctionRunHashId}";
  public static final String LATEST = "{latest}";

  public static final String ASSIGNABLE = "{assignable}";

  private Map<String, String> requestParams = new HashMap<>();

  public Map<String, String> getRequestParams() {
    return requestParams;
  }

  public RequestParams setSitePid(String value) {
    requestParams.put(SITE_PID, value);
    return this;
  }

  public RequestParams setSiteDcn(String value) {
    requestParams.put(SITE_DCN, value);
    return this;
  }

  public RequestParams setSellerPid(String value) {
    requestParams.put(SELLER_PID, value);
    return this;
  }

  public RequestParams setSellerSeatPid(String value) {
    requestParams.put(SELLERSEAT_PID, value);
    return this;
  }

  public RequestParams setSellerSeatRulePid(String value) {
    requestParams.put(SELLERSEATRULE_PID, value);
    return this;
  }

  public RequestParams setPublisherPid(String value) {
    requestParams.put(PUBLISHER_PID, value);
    return this;
  }

  public RequestParams setDetail(String value) {
    requestParams.put(DETAIL, value);
    return this;
  }

  public RequestParams setAdvertiserPid(String value) {
    requestParams.put(ADVERTISER_PID, value);
    return this;
  }

  public RequestParams setPrefix(String value) {
    requestParams.put(PREFIX, value);
    return this;
  }

  public RequestParams setTagPid(String value) {
    requestParams.put(TAG_PID, value);
    return this;
  }

  public RequestParams setTagValuesPid(String value) {
    requestParams.put(TAG_VALUES_PID, value);
    return this;
  }

  public RequestParams setCategoryPid(String value) {
    requestParams.put(CATEGORY_PID, value);
    return this;
  }

  public RequestParams setTagMappingsPid(String value) {
    requestParams.put(TAG_MAPPINGS_PID, value);
    return this;
  }

  public RequestParams setDateFrom(String value) {
    requestParams.put(START, value);
    return this;
  }

  public RequestParams setDateTo(String value) {
    requestParams.put(STOP, value);
    return this;
  }

  public RequestParams setPositionPid(String value) {
    requestParams.put(POSITION_PID, value);
    return this;
  }

  public RequestParams setPositionId(String value) {
    requestParams.put(POSITION_ID, value);
    return this;
  }

  public RequestParams setPlacementName(String value) {
    requestParams.put(PLACEMENT_NAME, value);
    return this;
  }

  public RequestParams setTargetType(String value) {
    requestParams.put(TARGET_TYPE, value);
    return this;
  }

  public RequestParams setAdsourcePid(String value) {
    requestParams.put(ADSOURCE_PID, value);
    return this;
  }

  public RequestParams setBuyerPid(String value) {
    requestParams.put(BUYER_PID, value);
    return this;
  }

  public RequestParams setRtbLibraryPid(String value) {
    requestParams.put(PROFILE_LIBRARY_PID, value);
    return this;
  }

  public RequestParams setDealPid(String value) {
    requestParams.put(DEAL_PID, value);
    return this;
  }

  public RequestParams set(String value) {
    requestParams.put(DEAL_PID, value);
    return this;
  }

  public RequestParams setProfilePid(String value) {
    requestParams.put(PROFILE_PID, value);
    return this;
  }

  public RequestParams setBidderConfigPid(String value) {
    requestParams.put(BIDDER_CONFIG_PID, value);
    return this;
  }

  public RequestParams setGroupPid(String value) {
    requestParams.put(RTBPROFILE_GROUP_PID, value);
    return this;
  }

  public RequestParams setStatus(String value) {
    requestParams.put(STATUS, value);
    return this;
  }

  public RequestParams setCampaignId(String value) {
    requestParams.put(CAMPAIGN_ID, value);
    return this;
  }

  public RequestParams setInterval(String value) {
    requestParams.put(INTERVAL, value);
    return this;
  }

  public RequestParams setUserPid(String value) {
    requestParams.put(USER_PID, value);
    return this;
  }

  public RequestParams setCompanyPid(String value) {
    requestParams.put(COMPANY_PID, value);
    return this;
  }

  public RequestParams setReportDrilldown(String value) {
    requestParams.put(REPORT_DRILLDOWN, value);
    return this;
  }

  public RequestParams setReportFilter(String value) {
    requestParams.put(REPORT_FILTER, value);
    return this;
  }

  public RequestParams setHandshakePid(String value) {
    requestParams.put(HANDSHAKE_PID, value);
    return this;
  }

  public RequestParams setHandshakeParams(String value) {
    requestParams.put(HANDSHAKE_PARAMS, value);
    return this;
  }

  public RequestParams setTierPid(String value) {
    requestParams.put(TIER_PID, value);
    return this;
  }

  public RequestParams setUsername(String value) {
    requestParams.put(USERNAME, value);
    return this;
  }

  public RequestParams setPassword(String value) {
    requestParams.put(PASSWORD, value);
    return this;
  }

  public RequestParams setCompanyId(String value) {
    requestParams.put(COMPANY_ID, value);
    return this;
  }

  public RequestParams setReportId(String value) {
    requestParams.put(REPORT_ID, value);
    return this;
  }

  public RequestParams setTxId(String value) {
    requestParams.put(TX_ID, value);
    return this;
  }

  public RequestParams setRevisionNumber(String value) {
    requestParams.put(REVISION_NUMBER, value);
    return this;
  }

  public RequestParams setTargetSite(String value) {
    requestParams.put(TARGET_SITE, value);
    return this;
  }

  public RequestParams setTargetPosition(String value) {
    requestParams.put(TARGET_POSITION, value);
    return this;
  }

  public RequestParams setPrimaryId(String value) {
    requestParams.put(PRIMARY_ID, value);
    return this;
  }

  public RequestParams setPrimaryName(String value) {
    requestParams.put(PRIMARY_NAME, value);
    return this;
  }

  public RequestParams setSecondaryId(String value) {
    requestParams.put(SECONDARY_ID, value);
    return this;
  }

  public RequestParams setSecondaryName(String value) {
    requestParams.put(SECONDARY_NAME, value);
    return this;
  }

  public RequestParams setBidderPid(String value) {
    requestParams.put(BIDDER_PID, value);
    return this;
  }

  public RequestParams setCreativeId(String value) {
    requestParams.put(CREATIVE_ID, value);
    return this;
  }

  public RequestParams setRulePid(String value) {
    requestParams.put(RULE_PID, value);
    return this;
  }

  public RequestParams setRulePids(String value) {
    requestParams.put(RULE_PIDS, value);
    return this;
  }

  public RequestParams setRtbPid(String value) {
    requestParams.put(RTB_PID, value);
    return this;
  }

  public RequestParams setBuyerGroupPid(String value) {
    requestParams.put(BUYER_GROUP_PID, value);
    return this;
  }

  public RequestParams setBuyerSeatPid(String value) {
    requestParams.put(BUYER_SEAT_PID, value);
    return this;
  }

  public RequestParams setRuleType(String value) {
    requestParams.put(RULE_TYPE, value);
    return this;
  }

  public RequestParams setSize(String value) {
    requestParams.put(SIZE, value);
    return this;
  }

  public RequestParams setPage(String value) {
    requestParams.put(PAGE, value);
    return this;
  }

  public RequestParams setqt(String value) {
    requestParams.put(QT, value);
    return this;
  }

  public RequestParams setqf(String value) {
    requestParams.put(QF, value);
    return this;
  }

  public RequestParams setqo(String value) {
    requestParams.put(QO, value);
    return this;
  }

  public RequestParams setSiteType(String siteType) {
    requestParams.put(SITE_TYPE, siteType);
    return this;
  }

  public RequestParams setPlacementTypes(String placementType) {
    requestParams.put(PLACEMENT_TYPE, placementType);
    return this;
  }

  public RequestParams setAttributePid(String value) {
    requestParams.put(ATTRIBUTE_PID, value);
    return this;
  }

  public RequestParams setAttributeValuePid(String value) {
    requestParams.put(ATTRIBUTE_VALUE_PID, value);
    return this;
  }

  public RequestParams setHbPartnerPid(String value) {
    requestParams.put(HBPARTNER_PID, value);
    return this;
  }

  public RequestParams setActionType(String value) {
    requestParams.put(ACTION_TYPE, value);
    return this;
  }

  public RequestParams setKeyValuePair(String pid) {
    requestParams.put(KEY_VALUE_PAIR_PID, pid);
    return this;
  }

  public RequestParams setPlacementId(String value) {
    requestParams.put(PLACEMENT_ID, value);
    return this;
  }

  public RequestParams setCompoundSegmentId(String value) {
    requestParams.put(COMPOUND_SEGMENT_ID, value);
    return this;
  }

  public RequestParams setStartDate(String value) {
    requestParams.put(START_DATE, value);
    return this;
  }

  public RequestParams setStopDate(String value) {
    requestParams.put(STOP_DATE, value);
    return this;
  }

  public RequestParams setPlacementMemo(String value) {
    requestParams.put(PLACEMENT_MEMO, value);
    return this;
  }

  public RequestParams setName(String value) {
    requestParams.put(NAME, value);
    return this;
  }

  public RequestParams setPids(String value) {
    requestParams.put(PIDS, value);
    return this;
  }

  public RequestParams setFeeAdjustmentPid(String value) {
    requestParams.put(FEE_ADJUSTMENT_PID, value);
    return this;
  }

  public RequestParams setFeeAdjustmentEnabled(String value) {
    requestParams.put(FEE_ADJUSTMENT_ENABLED, value);
    return this;
  }

  public RequestParams setExperimentPid(String value) {
    requestParams.put(EXPERIMENT_PID, value);
    return this;
  }

  public RequestParams setMinimal(String value) {
    requestParams.put(MIMIMAL, value);
    return this;
  }

  public RequestParams setVerificationListId(String value) {
    requestParams.put(VERIFICATION_LIST_PID, value);
    return this;
  }

  public RequestParams setAppVerificationId(String value) {
    requestParams.put(APP_VERIFICATION_ID, value);
    return this;
  }

  public RequestParams setAppAliasVerificationId(String value) {
    requestParams.put(APP_ALIAS_VERIFICATION_ID, value);
    return this;
  }

  public RequestParams setDomainVerificationId(String value) {
    requestParams.put(DOMAIN_VERIFICATION_ID, value);
    return this;
  }

  public RequestParams setPostAuctionDiscountPid(String value) {
    requestParams.put(POST_AUCTION_DISCOUNT_PID, value);
    return this;
  }

  public RequestParams setPostAuctionDiscountEnabled(String value) {
    requestParams.put(POST_AUCTION_DISCOUNT_ENABLED, value);
    return this;
  }

  public RequestParams setFilterListId(String value) {
    requestParams.put(FILTER_LIST_ID, value);
    return this;
  }

  public RequestParams setSegmentIds(String value) {
    requestParams.put(SEGMENT_IDS, value);
    return this;
  }

  public RequestParams setFilePid(String value) {
    requestParams.put(FILE_PID, value);
    return this;
  }

  public RequestParams setAdNotificationPid(String value) {
    requestParams.put(AD_NOTIFICATION_PID, value);
    return this;
  }

  public RequestParams setFetch(String value) {
    requestParams.put(FETCH, value);
    return this;
  }

  public RequestParams setSort(String value) {
    requestParams.put(SORT, value);
    return this;
  }

  public RequestParams setCompanyType(String value) {
    requestParams.put(COMPANY_TYPE, value);
    return this;
  }

  public RequestParams setFormat(String value) {
    requestParams.put(FORMAT, value);
    return this;
  }

  public RequestParams setBidderId(String value) {
    requestParams.put(BIDDER_ID, value);
    return this;
  }

  public RequestParams setAuctionRunHashId(String value) {
    requestParams.put(AUCTION_RUN_HASH_ID, value);
    return this;
  }

  public RequestParams setLatest(String value) {
    requestParams.put(LATEST, value);
    return this;
  }

  public RequestParams setAssignable(String value) {
    requestParams.put(ASSIGNABLE, value);
    return this;
  }
}
