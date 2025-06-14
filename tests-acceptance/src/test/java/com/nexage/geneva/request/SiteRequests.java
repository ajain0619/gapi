package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.SiteIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteRequests {

  @Autowired private Request request;

  public Request getCreateSiteRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectCreate)
        .setUrlPattern("/sellers/" + RequestParams.SELLER_PID + "/sites/");
  }

  public Request getCreateSitePssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectCreatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectCreatePss)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/site/");
  }

  public Request getCreateSitePssWithDetailRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectCreatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectCreatePss)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/site?detail=true");
  }

  public Request getCreateSitePssWithoutDetailRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectCreatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectCreatePss)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/site?detail=false");
  }

  public Request getCreateSiteProvisionRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectCreateProvision)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectCreateProvision)
        .setUrlPattern("/provision/company/" + RequestParams.COMPANY_ID + "/site/");
  }

  public Request getUpdateSiteRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectUpdate)
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID);
  }

  public Request getUpdateSiteSecondRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualObjectUpdate)
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID + "?txid=" + RequestParams.TX_ID);
  }

  public Request getUpdateSitePssRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "?txIdSiteUpdate="
                + RequestParams.TX_ID);
  }

  public Request getUpdateSitePssWithDetailRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "?detail=true&txIdSiteUpdate="
                + RequestParams.TX_ID);
  }

  public Request getUpdateSitePssWithoutDetailRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectUpdatePss)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "?detail=false&txIdSiteUpdate="
                + RequestParams.TX_ID);
  }

  public Request getUpdateSiteRequestProvision() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectGet)
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectGet)
        .setUrlPattern(
            "/provision/company/" + RequestParams.COMPANY_ID + "/site/" + RequestParams.SITE_DCN);
  }

  public Request getSiteUpdateInfo() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/siteUpdateInfo");
  }

  public Request getSiteUpdateInfoDetails() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/siteUpdateInfo?detail="
                + RequestParams.DETAIL);
  }

  public Request getGetSellerSiteSummariesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers/" + RequestParams.SELLER_PID + "/sitesummaries");
  }

  public Request getGetAllSiteSummariesRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/sellers/sitesummaries");
  }

  public Request getGetSiteRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectGet)
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectGet)
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID);
  }

  public Request getGetAllSitesPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/site/");
  }

  public Request getGetSitePssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/site/" + RequestParams.SITE_PID);
  }

  public Request getGetSitePssWithDetailRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectPssGet)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "?detail="
                + RequestParams.DETAIL);
  }

  public Request getGetSiteRequestProvision() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/provision/company/" + RequestParams.COMPANY_ID + "/site/" + RequestParams.SITE_DCN);
  }

  public Request getDeleteSiteRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID);
  }

  public Request getDeleteSiteProvisionRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/provision/company/" + RequestParams.COMPANY_ID + "/site/" + RequestParams.SITE_DCN);
  }

  public Request getGetSiteByPrefixRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers?prefix=" + RequestParams.PREFIX);
  }

  public Request getGetSiteAfterArchiveRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID);
  }

  public Request getSiteAuditRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/revision"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getGetSiteAuditForRevisionRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(SiteIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/revision/"
                + RequestParams.REVISION_NUMBER);
  }

  public Request getGetSiteDealTermsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers/sites/sitedealterms?sellerPid=" + RequestParams.SELLER_PID);
  }

  public Request getUpdateSiteDealTermsRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sellers/sites/setdefaultdealterm?sellerPid=" + RequestParams.SELLER_PID);
  }

  public Request getGetSiteLimit() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/" + RequestParams.COMPANY_PID + "/checkLimit/sites?" + RequestParams.SITE_PID);
  }
}
