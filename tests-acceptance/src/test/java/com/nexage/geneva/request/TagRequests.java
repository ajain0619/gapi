package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.TagIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagRequests {

  @Autowired private Request request;

  public Request getGetSiteTagsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID);
  }

  public Request getCreateNonExchangeTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateNonExchangeTagNonPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateNonExchangeTagNonPss)
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID + "/tags");
  }

  public Request getCreateExchangeTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateExchangeTagNonPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateExchangeTagNonPss)
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID + "/exchangetags");
  }

  public Request getUpdateNonExchangeTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectUpdateNonExchangeTagNonPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectUpdateNonExchangeTagNonPss)
        .setUrlPattern("/sellers/sites/tags/" + RequestParams.TAG_PID);
  }

  public Request getDeleteNonExchangeTagRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/sellers/sites/" + RequestParams.SITE_PID + "/tags/" + RequestParams.TAG_PID);
  }

  public Request getDeleteExchangeTagRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/sellers/sites/" + RequestParams.SITE_PID + "/tags/" + RequestParams.TAG_PID);
  }

  public Request getUpdateExchangeTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectUpdateExchangeTagNonPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectUpdateExchangeTagNonPss)
        .setUrlPattern("/sellers/sites/exchangetags/" + RequestParams.TAG_PID);
  }

  public Request getDeleteProvisionTagRequest() {
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
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getGetTagSummariesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.PUBLISHER_PID
                + "/tagsummary"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getGetBidderPerformanceRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/biddersPerformanceSummary"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getGetPublisherTagsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag");
  }

  public Request getGetPublisherTagRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getGetPublisherAdnetsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/buyer/");
  }

  public Request getGetProvisionTagRequest() {
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
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getCreatePublisherRtbTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateRtbTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateRtbTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag");
  }

  public Request getCreateMultiplePublisherRtbTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateRtbTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateRtbTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/multiTags");
  }

  public Request getCreateMultiplePublisherMedTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateMedTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateMedTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/multiTags");
  }

  public Request getCreatePublisherMedTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCreateMedTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCreateMedTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag");
  }

  public Request getCreateProvisionTagRequest() {
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
                + "/tag/");
  }

  public Request getUpdatePublisherMedTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectUpdateMedTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectUpdateMedTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getUpdatePublisherRtbTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectUpdateRtbTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectUpdateRtbTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getUpdateProvisionTagRequest() {
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
                + "/tag/"
                + RequestParams.TAG_PID);
  }

  public Request getGetArchivedTagDeploymentRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/sellers/sites/" + RequestParams.SITE_PID + "/tagdeployment/" + RequestParams.TAG_PID);
  }

  public Request getTagAuditRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "/revision"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getTagAuditRequestForRevision() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "/revision/"
                + RequestParams.REVISION_NUMBER);
  }

  public Request getCopyPublisherRtbTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCloneRtbTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCloneRtbTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "?method=clone&targetSite="
                + RequestParams.TARGET_SITE
                + "&targetPosition="
                + RequestParams.TARGET_POSITION);
  }

  public Request getCopyPublisherMedTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.expectedObjectCloneMedTagPss)
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualObjectCloneMedTagPss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "?method=clone&targetSite="
                + RequestParams.TARGET_SITE
                + "&targetPosition="
                + RequestParams.TARGET_POSITION);
  }

  public Request getValidateTagRequest() {

    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/tag?"
                + "adnet="
                + RequestParams.ADSOURCE_PID
                + "&pid="
                + RequestParams.PRIMARY_ID
                + "&pname="
                + RequestParams.PRIMARY_NAME
                + "&sid="
                + RequestParams.SECONDARY_ID
                + "&sname="
                + RequestParams.SECONDARY_NAME);
  }

  public Request getGetTagPerformanceMetricsPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "/tagPerformanceMetrics/archiveTransaction");
  }

  public Request getTagArchivePssRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setActualObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectTagArchive)
        .setExpectedObjectIgnoredKeys(TagIgnoredKeys.actualAndExpectedObjectTagArchive)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/tag/"
                + RequestParams.TAG_PID
                + "?txid="
                + RequestParams.TX_ID);
  }

  public Request getAdsourceTagRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/adsource/"
                + RequestParams.ADSOURCE_PID
                + "/tag");
  }

  public Request getAdsourceTagMetricRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/adsource/"
                + RequestParams.ADSOURCE_PID
                + "/tag/metric");
  }
}
