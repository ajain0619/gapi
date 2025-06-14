package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.model.crud.AdSource;
import com.nexage.geneva.request.AdSourceDefaultsRequests;
import com.nexage.geneva.request.KpiBoxesAndChartingRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.ErrorHandler;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.geneva.AdSourceType;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

public class AdSourceDefaultsSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private AdSourceDefaultsRequests adSourceDefaultsRequests;
  @Autowired public KpiBoxesAndChartingRequests kpiBoxesAndChartingRequests;

  private String adSourcePid;
  private AdSourceType adSourceType;
  private JSONObject expectedAdSourceDefaults;
  public Map<String, AdSource> adSourceMap;

  private static final String ANY_PUBLISHER = "0";
  private static final String ANY_MEDIATION_AD_SOURCE = "00";
  public JSONResource adsrcResp;
  public JSONArray adsrcResp2;

  @Given("^\"(.+?)\" ad source type is selected")
  public void ad_sources_type_is_selected(AdSourceType adSourceType) throws Throwable {
    this.adSourceType = adSourceType;
  }

  @Given("^ad source summaries are retrieved$")
  public void ad_source_summaries_are_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        kpiBoxesAndChartingRequests
            .getGetPublisherCompleteMetricsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    ErrorHandler.assertNotNull(
        commonSteps.request, commonSteps.serverResponse, "Could not retrieve dashboard summary");

    adSourceMap = getAdSourcesMap(commonSteps.serverResponse);
    assertTrue(adSourceMap.size() > 0, "Ad Sources are not found");
  }

  @Given("^the user selects ad source named \"(.+?)\"$")
  public void the_user_selects_ad_source_named(String adSourceName) throws Throwable {
    assertNotNull(adSourceMap, "Ad Sources are not retrieved.");

    adSourcePid = adSourceMap.get(adSourceName).getPid();
    assertNotNull("Ad Source " + adSourceName + " doesn't exist.", adSourcePid);
  }

  @When("^the user gets all ad source defaults$")
  public void the_user_gets_all_ad_source_defaults() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();
    if (adSourceType == AdSourceType.MEDIATION) {
      commonSteps.request = adSourceDefaultsRequests.getGetAllMediationAdSourceDefaultsRequest();
    } else if (adSourceType == AdSourceType.RTB) {
      commonSteps.request = adSourceDefaultsRequests.getGetRtbAdSourceDefaultsRequest();
    }
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    // mx-463 start
    if (commonSteps.serverResponse != null) {
      adsrcResp = commonSteps.serverResponse;
    }
  }

  public String ADsourceResp(String adres) throws Throwable {
    JSONObject adsource;
    adsrcResp2 = adsrcResp.array();
    for (int i = 0; i < adsrcResp2.length(); i++) {
      adsource = adsrcResp2.getJSONObject(i);
      adres = adsource.getString("adSourcePid");
    }
    return adres;
  }
  // mx-463 end

  @When("^the user gets mediation ad source defaults$")
  public void the_user_gets_mediation_ad_source_defaults() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setAdsourcePid(adSourcePid)
            .getRequestParams();
    commonSteps.request =
        adSourceDefaultsRequests
            .getGetMediationAdSourceDefaultsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates ad source defaults from the json file \"(.+?)\"$")
  public void the_user_updates_ad_source_defaults_from_the_json_file(String filename)
      throws Throwable {
    expectedAdSourceDefaults = JsonHandler.getJsonObjectFromFile(filename);

    if (adSourceType == AdSourceType.MEDIATION) {
      commonSteps.requestMap =
          new RequestParams()
              .setSellerPid(commonSteps.companyPid)
              .setAdsourcePid(adSourcePid)
              .getRequestParams();
      commonSteps.request = adSourceDefaultsRequests.getUpdateMediationAdSourceDefaultsRequest();
    } else if (adSourceType == AdSourceType.RTB) {
      commonSteps.requestMap =
          new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();
      commonSteps.request = adSourceDefaultsRequests.getUpdateRtbAdSourceDefaultsRequest();
    }
    commonSteps
        .request
        .setRequestPayload(expectedAdSourceDefaults)
        .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes mediation ad source defaults$")
  public void the_user_deletes_mediation_ad_source_defaults() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setAdsourcePid(adSourcePid)
            .getRequestParams();
    commonSteps.request =
        adSourceDefaultsRequests
            .getDeleteMediationAdSourceDefaultsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates mediation ad source defaults from the json file \"(.+?)\"$")
  public void the_user_creates_mediation_ad_source_defaults_from_the_json_file(String filename)
      throws Throwable {
    expectedAdSourceDefaults = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setAdsourcePid(adSourcePid)
            .getRequestParams();
    commonSteps.request =
        adSourceDefaultsRequests
            .getCreateMediationAdSourceDefaultsRequest()
            .setRequestPayload(expectedAdSourceDefaults)
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets mediation ad source defaults of another publisher$")
  public void the_user_gets_mediation_ad_source_defaults_of_another_publisher() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(ANY_PUBLISHER)
            .setAdsourcePid(ANY_MEDIATION_AD_SOURCE)
            .getRequestParams();
    commonSteps.request =
        adSourceDefaultsRequests
            .getGetMediationAdSourceDefaultsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects ad source with pid \"(.+?)\"$")
  public void the_user_selects_ad_source_with_pid(String adSourcePid) throws Throwable {
    this.adSourcePid = adSourcePid;
  }

  private Map<String, AdSource> getAdSourcesMap(JSONResource resource) throws Throwable {
    Map<String, AdSource> adSourcesMap = new HashMap<>();
    JSONArray adSources = resource.object().getJSONArray(JsonField.ADSOURCES);
    for (int i = 0; i < adSources.length(); i++) {
      JSONObject adSource = adSources.getJSONObject(i);
      AdSource adsource =
          new AdSource(adSource.getString(JsonField.PID), adSource.getString(JsonField.NAME));
      adSourcesMap.put(adsource.getName(), adsource);
    }
    return adSourcesMap;
  }
}
