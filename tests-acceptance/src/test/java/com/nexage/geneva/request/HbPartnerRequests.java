package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HbPartnerRequests {

  @Autowired private Request request;

  private final String hbPartnerBasicUrl = "/v1/hbpartners";
  private final String hbPartnerRequestParamsUrl = "/v1/hbpartners/{hbPartnerPid}";

  public Request getCreateHbPartnerRequest() {
    return request.clear().setPostStrategy().setUrlPattern(hbPartnerBasicUrl);
  }

  public Request getGetAllHbPartnersRequest() {
    return request.clear().setGetStrategy().setUrlPattern(hbPartnerBasicUrl);
  }

  public Request getGetHbPartnersRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(hbPartnerBasicUrl + "?detail=" + RequestParams.DETAIL);
  }

  public Request getGetHbPartnersForPublisherRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            hbPartnerBasicUrl
                + "?sellerId="
                + RequestParams.PUBLISHER_PID
                + "&detail="
                + RequestParams.DETAIL);
  }

  public Request getGetHbPartnersForSiteRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            hbPartnerBasicUrl
                + "?siteId="
                + RequestParams.SITE_PID
                + "&detail="
                + RequestParams.DETAIL);
  }

  public Request getGetOneHbPartnerRequest() {
    return request.clear().setGetStrategy().setUrlPattern(hbPartnerRequestParamsUrl);
  }

  public Request getUpdateHbPartnerRequest() {
    return request.clear().setPutStrategy().setUrlPattern(hbPartnerRequestParamsUrl);
  }

  public Request getDeactivateHbPArtnerRequest() {
    return request.clear().setDeleteStrategy().setUrlPattern(hbPartnerRequestParamsUrl);
  }
}
