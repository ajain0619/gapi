package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.PositionIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PositionRequests {

  @Autowired private Request request;

  public Request getCreatePositionRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID + "/positions")
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectCreatePositionNonPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectCreatePositionNonPss);
  }

  public Request getCreatePositionRequestNexageUser() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectCreatePositionNonPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectCreatePositionNonPss);
  }

  public Request getCreatePositionSecondRequestRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sellers/sites/" + RequestParams.SITE_PID + "?txid=" + RequestParams.TX_ID)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectCreatePositionNonPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectCreatePositionNonPss);
  }

  public Request getUpdatePositionRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sellers/sites/positions/" + RequestParams.POSITION_PID)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectUpdatePositionNonPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectUpdatePositionNonPss);
  }

  public Request getUpdatePositionMediationRulesSetup() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/sellers/sites/" + RequestParams.SITE_PID + "/positions/updateMediationRules")
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectUpdatePositionNonPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectUpdatePositionNonPss);
  }

  public Request getDeletePositionRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/sellers/sites/"
                + RequestParams.SITE_PID
                + "/positions/"
                + RequestParams.POSITION_PID);
  }

  public Request getGetAllPositionsPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position");
  }

  public Request getGetPositionPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID);
  }

  public Request getGetPositionPssDetailRequest() {
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
                + "?detail="
                + RequestParams.DETAIL);
  }

  public Request getGetDetaledPositionPssRequest() {
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
                + "/detailedPosition");
  }

  public Request getCreatePositionPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position?detail="
                + RequestParams.DETAIL)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectCreatePlacementPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectCreatePlacementPss);
  }

  public Request getCreatePositionProvisionRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position")
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectCreatePlacementProvision)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectCreatePlacementProvision);
  }

  public Request getUpdatePositionPssRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectUpdatePlacementPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectUpdatePlacementPss);
  }

  public Request getUpdatePositionWithDetail() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "?detail="
                + RequestParams.DETAIL)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectUpdatePlacementPss)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectUpdatePlacementPss);
  }

  public Request getUpdatePositionProvisionRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualObjectUpdatePlacementProvision)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.expectedObjectUpdatePlacementProvision);
  }

  public Request getGetPositionProvisionRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID);
  }

  public Request getDeletePositionProvisionRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/provision/company/"
                + RequestParams.COMPANY_ID
                + "/site/"
                + RequestParams.SITE_DCN
                + "/position/"
                + RequestParams.POSITION_PID);
  }

  public Request getGetPositionPerformanceMetricsPssRequest() {
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
                + "/positionPerformanceMetrics/archiveTransaction");
  }

  public Request getArchivePositionPssRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectArchive)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectArchive)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "?txid="
                + RequestParams.TX_ID);
  }

  public Request getGetPositionAuditRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/revision"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getPositionAuditRequestForRevision() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectAudit)
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedObjectAudit)
        .setUrlPattern(
            "/audit/publisher/"
                + RequestParams.COMPANY_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "/revision/"
                + RequestParams.REVISION_NUMBER);
  }

  public Request getClonePositionRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setActualObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedPositionClone)
        .setExpectedObjectIgnoredKeys(PositionIgnoredKeys.actualAndExpectedPositionClone)
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/position/"
                + RequestParams.POSITION_PID
                + "?operation=clone&targetSite="
                + RequestParams.TARGET_SITE);
  }
}
