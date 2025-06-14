package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.TierIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TierRequests {

  @Autowired private Request request;

  public Request getModifyTierNonPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysModifyTierNonPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysModifyTierNonPss)
        .setUrlPattern(
            "/sellers/sites/" + RequestParams.SITE_PID + "/positions/updateMediationRules");
  }

  public Request getCreateTierPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier");
  }

  public Request getCreateTierProvisionRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier");
  }

  public Request getCreateDecisionMakerTierPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/decisionMaker");
  }

  public Request getUpdateDecisionMakerTierPssRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/decisionMaker");
  }

  public Request getGetDecisionMakerTierPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/decisionMaker");
  }

  public Request getDeleteTierPssRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysCreateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getDeleteTierProvisionRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getUpdateTierPssRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysUpdateTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeysUpdateTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getUpdateTierProvisionRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getGetTierPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeyGetTierPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeyGetTierPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getGetTierProvisionRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID);
  }

  public Request getGetAllTiersPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(TierIgnoredKeys.ignoredKeyGetTiersPss)
        .setActualObjectIgnoredKeys(TierIgnoredKeys.ignoredKeyGetTiersPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/");
  }

  public Request getGetAllTiersProvisionRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/");
  }

  public Request getAssignTagProvisionRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getUnassignTagProvisionRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tier/"
                + RequestParams.TIER_PID
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getSortTiersProvisionRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tiers-sort");
  }
}
