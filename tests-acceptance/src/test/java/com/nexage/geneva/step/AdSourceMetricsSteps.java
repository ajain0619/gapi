package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.model.crud.AdSource;
import com.nexage.geneva.request.AdNetRequests;
import com.nexage.geneva.request.AdSourceMetricsRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

public class AdSourceMetricsSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private AdSourceMetricsRequests adSourceMetricsRequests;
  @Autowired private AdNetRequests adNetRequests;
  @Autowired private TagSteps tagSteps;
  private Map<String, AdSource> adSourceMap = new HashMap<>();
  private String adSourcePid;
  private String sitePid;
  private String selectedPositionPid;
  private String tagName;
  private String dateFrom;
  private String dateTo;

  private String interval = "";

  @When("^the user gets the ad source with name \"(.+?)\"$")
  public void the_user_gets_all_ad_net_summaries(String adSourceName) throws Throwable {
    commonSteps.request = adNetRequests.getGetAllAdNetsRequest();
    commonFunctions.executeRequest(commonSteps);
    adSourceMap = getAdSourcesMap(commonSteps.serverResponse);
    assertNotNull(adSourceMap, "Ad Sources are not retrieved.");
    adSourcePid = adSourceMap.get(adSourceName).getPid();
    assertNotNull("Ad Source " + adSourceName + " doesn't exist.", adSourcePid);
  }

  @When("^the ad source summary interval is \"(.*?)\"$")
  public void the_ad_source_summary_interval_is(String interval) throws Throwable {
    this.interval = interval;
  }

  @Then("^the ad source metrics for ad source are retrieved$")
  public void the_ad_source_metrics_are_retrieved() throws Throwable {
    RequestParams requestParams =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setAdsourcePid(adSourcePid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo);
    if (!StringUtils.isEmpty(interval)) {
      requestParams.setInterval(interval);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        adSourceMetricsRequests
            .getGetAdSourceMetricsForAdSourceRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the ad source metrics for ad source and site are retrieved$")
  public void the_ad_source_metrics_for_ad_source_and_site_are_retrieved() throws Throwable {
    RequestParams requestParams =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setAdsourcePid(adSourcePid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo);
    if (!StringUtils.isEmpty(interval)) {
      requestParams.setInterval(interval);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        adSourceMetricsRequests
            .getGetAdSourceMetricsForSiteAndAdSourceRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the ad source metrics for ad source, site and position are retrieved$")
  public void the_ad_source_metrics_for_ad_source_site_and_position_are_retrieved()
      throws Throwable {
    RequestParams requestParams =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPlacementName(commonSteps.selectedPosition.getString(JsonField.NAME))
            .setAdsourcePid(adSourcePid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo);
    if (!StringUtils.isEmpty(interval)) {
      requestParams.setInterval(interval);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        adSourceMetricsRequests
            .getGetAdSourceMetricsForSiteAndPositionAndAdSourceRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the ad source metrics for ad source, site, position and tag are retrieved$")
  public void the_ad_source_metrics_for_ad_source_site_and_position_and_tag_are_retrieved()
      throws Throwable {
    RequestParams requestParams =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPlacementName(commonSteps.selectedPosition.getString(JsonField.NAME))
            .setTagPid(commonSteps.selectedTag.getString(JsonField.PID))
            .setAdsourcePid(adSourcePid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo);
    if (!StringUtils.isEmpty(interval)) {
      requestParams.setInterval(interval);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        adSourceMetricsRequests
            .getGetAdSourceMetricsForSiteAndPositionAndTagAndAdSourceRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private Map<String, AdSource> getAdSourcesMap(JSONResource resource) throws Throwable {
    Map<String, AdSource> adSourcesMap = new HashMap<>();
    JSONArray adSources = resource.array();

    for (int i = 0; i < adSources.length(); i++) {
      JSONObject adSource = adSources.getJSONObject(i);
      AdSource adsource =
          new AdSource(adSource.getString(JsonField.PID), adSource.getString(JsonField.NAME));
      adSourcesMap.put(adsource.getName(), adsource);
    }
    return adSourcesMap;
  }
}
