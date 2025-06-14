package com.nexage.geneva.step;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.AdSource;
import com.nexage.geneva.request.AdNetRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

@Log4j2
public class AdNetSteps {

  private static final String S3_BUCKET_LOGO_PATH = "logos/";

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private AdNetRequests adNetRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<String, AdSource> adSourceMap = new HashMap<>();
  private Map<String, String> requestParams;

  private JSONObject adSourceChanges;
  private String adSourceChangesPid;
  private AdSource adSource;

  String logo;
  String logo_url;

  private static final String INVALID_PID = "incorrect";
  private static final String DELETED_STATUS = "DELETED";
  private static String cridHeaderField, adnetName;
  private static String EXTENSION = "jpg";

  @When("^the user gets all ad net summaries$")
  public void the_user_gets_all_ad_net_summaries() throws Throwable {
    commonSteps.request = adNetRequests.getGetAllAdNetsRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets ad nets for selected buyer$")
  public void the_user_gets_ad_nets_for_selected_buyer() throws Throwable {
    requestParams =
        new RequestParams().setBuyerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        adNetRequests.getGetAllAdNetsForBuyerRequest().setRequestParams(requestParams);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      JSONArray adSourceArray = commonSteps.serverResponse.array();
      for (int i = 0; i < adSourceArray.length(); i++) {
        AdSource adSource =
            TestUtils.mapper.readValue(adSourceArray.get(i).toString(), AdSource.class);
        adSourceMap.put(adSource.getName(), adSource);
      }
      assertTrue(adSourceMap.size() > 0, "Ad Sources are not found");
    }
  }

  @When("^the user selects ad net \"(.+?)\"$")
  public void the_user_selects_ad_net(String name) throws Throwable {
    getAdNet(name);
  }

  @When("^the user creates ad net from the json file \"(.+?)\"$")
  public void the_user_creates_ad_net_from_the_json_file(String filepath) throws Throwable {
    requestParams =
        new RequestParams().setBuyerPid(commonSteps.getCompany().getPid()).getRequestParams();
    adSourceChanges = JsonHandler.getJsonObjectFromFile(filepath);
    commonSteps.request =
        adNetRequests
            .getCreateAdNetRequest()
            .setRequestPayload(adSourceChanges)
            .setRequestParams(requestParams);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the value of \"(.+?)\" in the database matches with the server response$")
  public void the_value_of_cridHeaderField_in_database_is_correct(String message) throws Throwable {
    String cridHeaderFieldDB = databaseUtils.getCridHeaderField(adnetName);
    if (isNull(cridHeaderField) || cridHeaderField.equals("null")) {
      assertNull(
          cridHeaderFieldDB,
          "cridHeaderField "
              + cridHeaderField
              + "doesn't match with database value "
              + cridHeaderFieldDB);
    } else {
      assertTrue(
          cridHeaderField.equals(cridHeaderFieldDB),
          "cridHeaderField"
              + cridHeaderField
              + "doesn't match with database value"
              + cridHeaderFieldDB);
    }
  }

  @Then("^returned adnet data matches the following json file \"(.+?)\"$")
  public void returned_adnet_data_matches_the_following_json_file(String filename)
      throws Throwable {
    String expectedJson = TestUtils.getResourceAsString(filename);
    JSONObject createdAdnet = commonSteps.serverResponse.toObject();
    adnetName = createdAdnet.getString(JsonField.NAME);
    try {
      cridHeaderField = createdAdnet.getString(JsonField.CRID_HEADER_FIELD);
    } catch (JSONException exception) {
      log.debug("createdAdnet does not include [cridHeaderField] field.");
      cridHeaderField = null;
    }
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request, commonSteps.serverResponse, "adnet", expectedJson);
  }

  @When("^the user updates selected ad net from the json file \"(.+?)\"$")
  public void the_user_updates_selected_ad_net_from_the_json_file(String filename)
      throws Throwable {
    adSourceChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestParams =
        new RequestParams()
            .setBuyerPid(commonSteps.getCompany().getPid())
            .setAdsourcePid(adSourceChangesPid)
            .getRequestParams();
    commonSteps.request =
        adNetRequests
            .getUpdateAdNetRequest()
            .setRequestParams(requestParams)
            .setRequestPayload(adSourceChanges);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes ad net \"(.+?)\"$")
  public void the_user_deletes_ad_net(String name) throws Throwable {
    assertNotNull(adSourceMap, "Failed to get ad sources");

    adSource = adSourceMap.get(name);
    requestParams = new RequestParams().setAdsourcePid(adSource.getPid()).getRequestParams();
    commonSteps.request =
        adNetRequests
            .getDeleteAdNetRequest()
            .setRequestParams(requestParams)
            .setContentTypeRequestHeaders();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates not existing ad net$")
  public void the_user_updates_not_existing_ad_net() throws Throwable {
    adSourceChanges =
        new JSONObject() {
          {
            put(JsonField.NAME, INVALID_PID);
          }
        };
    requestParams =
        new RequestParams()
            .setBuyerPid(commonSteps.getCompany().getPid())
            .setAdsourcePid(INVALID_PID)
            .getRequestParams();
    commonSteps.request =
        adNetRequests
            .getUpdateAdNetRequest()
            .setRequestPayload(adSourceChanges)
            .setRequestParams(requestParams);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes not existing ad net$")
  public void the_user_deletes_not_existing_ad_net() throws Throwable {
    requestParams = new RequestParams().setAdsourcePid(INVALID_PID).getRequestParams();
    commonSteps.request =
        adNetRequests
            .getDeleteAdNetRequest()
            .setRequestParams(requestParams)
            .setContentTypeRequestHeaders();
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^ad net status changed to deleted$")
  public void ad_net_status_changed_to_deleted() throws Throwable {
    getAdNet(adSource.getName());
    assertTrue(
        commonSteps.serverResponse.get(JsonField.STATUS).equals(DELETED_STATUS),
        "Ad Source status hasn't changed to deleted");
  }

  private void getAdNet(String name) throws Throwable {
    assertNotNull(adSourceMap, "Failed to get ad sources");

    adSource = adSourceMap.get(name);
    requestParams = new RequestParams().setAdsourcePid(adSource.getPid()).getRequestParams();
    commonSteps.request = adNetRequests.getGetAdNetRequest().setRequestParams(requestParams);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      adSourceChangesPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  private void getAdNetFromPid(String pid) throws Throwable {
    requestParams = new RequestParams().setAdsourcePid(pid).getRequestParams();
    commonSteps.request = adNetRequests.getGetAdNetRequest().setRequestParams(requestParams);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      adSourceChangesPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @Then("^the logo and logoUrl are created correctly$")
  public void the_logo_and_logoUrl_are_created_correctly() throws Throwable {
    getLogoAndLogoUrl();
    // Match logo name pattern
    Pattern pattern =
        Pattern.compile(Integer.parseInt(adSource.getPid()) + "\\-[0-9]+\\." + EXTENSION);
    Matcher matcher = pattern.matcher(logo.replaceFirst("logos/", ""));

    assertTrue(matcher.matches());
  }

  @When("^the user gets the adnet with the newly created pid$")
  public void the_user_gets_the_adnet_with_the_newly_created_pid() throws Throwable {
    getAdNetFromPid(adSource.getPid());
  }

  @Then("^the returned logo and logUrl data is correct$")
  public void the_returned_logo_and_logUrl_data_is_correct() throws Throwable {
    ReadContext ctx = null;
    String jsonString = null;
    String returnedPid;
    String returnedlogo;
    String returnedlogoUrl;
    String requestUrl = commonSteps.request.getUrl();

    try {
      if (requestUrl.contains("dashboardsummary")
          || requestUrl.contains("adsourcesummaries")
          || requestUrl.contains("buyer/")) {
        if (requestUrl.contains("dashboardsummary")) {
          jsonString = commonSteps.serverResponse.object().toString();
          ctx = JsonPath.parse(jsonString);
          jsonString = ctx.read("$.adsources[?(@.pid==" + adSource.getPid() + ")]").toString();
        } else {
          jsonString = commonSteps.serverResponse.array().toString();
          ctx = JsonPath.parse(jsonString);
          jsonString = ctx.read("$.[?(@.pid==" + adSource.getPid() + ")]").toString();
        }
        ctx = JsonPath.parse(jsonString);
        returnedPid = ctx.read("$[0]['pid']").toString();
        returnedlogoUrl = ctx.read("$[0]['logoUrl']").toString();
        assertTrue(adSource.getPid().equals(returnedPid), "pid value does not match");
        assertTrue(logo_url.equals(returnedlogoUrl), "logo url value does not match");

      } else if (requestUrl.contains("tagsummary")) {
        jsonString = commonSteps.serverResponse.object().toString();
        ctx = JsonPath.parse(jsonString);
        jsonString =
            ctx.read(
                    "$.sites[*].positions[*].tags[?(@.adsourceName=='" + adSource.getName() + "')]")
                .toString();
        ctx = JsonPath.parse(jsonString);
        returnedlogoUrl = ctx.read("$[0]['adSourceLogoUrl']").toString();
        assertTrue(logo_url.equals(returnedlogoUrl), "logo url value does not match");
      } else {
        jsonString = commonSteps.serverResponse.object().toString();
        ctx = JsonPath.parse(jsonString);
        returnedPid = ctx.read("$.pid").toString();
        returnedlogo = ctx.read("$.logo").toString();
        returnedlogoUrl = ctx.read("$.logoUrl");
        assertTrue(adSource.getPid().equals(returnedPid), "pid value does not match");
        assertTrue(logo.equals(returnedlogo), "logo value does not match");
        assertTrue(logo_url.equals(returnedlogoUrl), "logo url value does not match");
      }
    } catch (ClassCastException e) {
      throw e;
    }
  }

  @Then("^the logo and logoUrl are null$")
  public void the_logo_and_logoUrl_are_null() throws Throwable {
    getLogoAndLogoUrl();
    assertTrue(logo == null, "logo should be null");
    assertTrue(logo_url == null, "logo url should be nul");
  }

  public void getLogoAndLogoUrl() throws IOException, JSONException {
    ReadContext ctx = JsonPath.parse(commonSteps.serverResponse.toObject().toString());
    adSource = new AdSource(ctx.read("$.pid").toString(), ctx.read("$.name").toString());
    logo = ctx.read("$.logo");
    if (nonNull(logo)) {
      logo = logo.replaceFirst(S3_BUCKET_LOGO_PATH, "");
    }
    logo_url = ctx.read("$.logoUrl");
    log.info("logo: {}, logo_url={}", logo, logo_url);
  }
}
