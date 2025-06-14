package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SitesRequest {

  @Autowired private Request request;

  private final String sitesMultiSearchBaseUrl = "/v1/sites?multiSearch";

  public Request getSellersSitesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.PUBLISHER_PID + "/sites");
  }

  public Request getSellersSitesRequestWithQueryTerms() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites?size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&qt="
                + RequestParams.QT);
  }

  public Request getSellersSitesRequestWithFetchParam(String fetch) {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.PUBLISHER_PID + "/sites?fetch=" + fetch);
  }

  public Request getSellersSitesRequestWithSiteFilters() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sellers/%s/sites?siteType=%s&status=%s",
                RequestParams.PUBLISHER_PID, RequestParams.SITE_TYPE, RequestParams.STATUS));
  }

  public Request getSellersSitesRequestForSeats() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sites?qf=%s&qt=%s&size=%s&page=%s",
                RequestParams.QF, RequestParams.QT, RequestParams.SIZE, RequestParams.PAGE));
  }

  public Request getSitesForSellerSeatRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/seller-seats/%s/sites?"
                    + "siteType=%s&status=%s&fetch=%s&minimal=%s&qt=%s&size=%s&page=%s",
                RequestParams.SELLERSEAT_PID,
                RequestParams.SITE_TYPE,
                RequestParams.STATUS,
                RequestParams.FETCH,
                RequestParams.MIMIMAL,
                RequestParams.QT,
                RequestParams.SIZE,
                RequestParams.PAGE));
  }

  public Request getSitesWithMultiSearch() {
    String urlPattern = sitesMultiSearchBaseUrl.concat("&qf={qf}&qo={qo}");
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(urlPattern);
  }

  public Request getSitesSummariesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sellers/"
                    + RequestParams.PUBLISHER_PID
                    + "/sites/summaries?size="
                    + RequestParams.SIZE
                    + "&page="
                    + RequestParams.PAGE
                    + "&startDate="
                    + RequestParams.START_DATE
                    + "&stopDate="
                    + RequestParams.STOP_DATE
                    + "&name="
                    + RequestParams.NAME
                    + "&pids="
                    + RequestParams.PIDS));
  }
}
