package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.request.PlacementsRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class PlacementSteps {

  @Autowired private PlacementsRequest placementsRequest;

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private DatabaseUtils databaseUtils;

  private Map<String, String> requestMap;

  @When("^The site pid is passed into grab \"(.+?)\" placements$")
  public void grabTheFirstPagePlacements(String size) throws Throwable {
    grabTheFirstPagePlacements(size, commonSteps.getCompany().getPid());
  }

  @When(
      "^The site pid is passed in to grab \"(.+)\" placements for the company with the company pid \"(.{1,20})\"$")
  public void grabTheFirstPagePlacements(String size, String companyPid) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setSize(size)
            .getRequestParams();
    commonSteps.request = placementsRequest.getPlacementRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^grab the second page of \"(.+?)\" placements \"(.+?)\" page$")
  public void grabTheSecondPagePlacements(String size, String page) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSize(size)
            .setSitePid(commonSteps.getSite().getPid())
            .setPage(page)
            .getRequestParams();
    commonSteps.request = placementsRequest.getPlacementRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" placements summaries from between \"(.+?)\" and \"(.+?)\"$")
  public void getPlacementsSumaries(String page, String size, String startDate, String endDate)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setPage(page)
            .setSize(size)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .getRequestParams();
    commonSteps.request =
        placementsRequest.getPlacementSummaryRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^The seller pid is pass into grab \"(.+)\" and the site pid \"(.+)\" grab all minimal placements$")
  public void getPlacementsWithMinimalData(String sellerPid, String sitePid) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(sellerPid)
            .setSitePid(sitePid)
            .setMinimal("true")
            .getRequestParams();
    commonSteps.request =
        placementsRequest.getPlacementRequestMinimal().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^The seller pid is pass into grab \"(.+)\" and the site pid \"(.+)\" grab all minimal placements with QT \"(.+)\"$")
  public void getPlacementsWithMinimalData(String sellerPid, String sitePid, String qt)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(sellerPid)
            .setSitePid(sitePid)
            .setMinimal("true")
            .setqt(qt)
            .getRequestParams();
    commonSteps.request =
        placementsRequest.getPlacementRequestMinimalQT().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^The seller pid is passed into grab \"(.+)\" and the query term \"(.+)\"$")
  public void grabThePlacementsUnderaQuery(String size, String qt) throws Throwable {
    grabThePlacementsUnderaQuery(commonSteps.getCompany().getPid(), size, qt);
  }

  @When("^the company pid \"(.{1,20})\" is passed in to grab \"(.+)\" and the query term \"(.+)\"$")
  public void grabThePlacementsUnderaQuery(String companyPid, String size, String qt)
      throws Throwable {
    requestMap =
        new RequestParams().setPublisherPid(companyPid).setSize(size).setqt(qt).getRequestParams();
    commonSteps.request = placementsRequest.getPlacementQueryRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^a request is made to list the placements for this company and placement type \"(.+)\" and status \"(.+)\"$")
  public void read_company_sites_with_filter_terms(final String placementType, final String status)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setPlacementTypes(placementType)
            .setStatus(status)
            .getRequestParams();
    commonSteps.request = placementsRequest.getPlacementRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user reads the placements for the site with the site pid \"(.{1,20})\" for the company with the company pid \"(.{1,20})\"$")
  public void test(String sitePid, String companyPid) throws Throwable {
    requestMap =
        new RequestParams().setPublisherPid(companyPid).setSitePid(sitePid).getRequestParams();
    commonSteps.request = placementsRequest.getPlacementRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates placement from the json file \"(.+?)\"$")
  public void the_user_creates_placement_from_the_json_file(String filename) throws Throwable {
    JSONObject expectedPlacement = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request =
        placementsRequest
            .getCreatePlacementRequest()
            .setRequestParams(requestMap)
            .setRequestPayload(expectedPlacement);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a placement banner with name \"(.+?)\" for site pid \"(.{1,20})\"$")
  public void the_user_creates_banner_placement_with_name_and_site(String name, String sitePid)
      throws Throwable {
    JSONObject expectedPlacement =
        JsonHandler.getJsonObjectFromFile(
            "jsons/genevacrud/placements/payload/CreatePlacementBanner_payload.json");
    expectedPlacement.put("name", name.toLowerCase());
    expectedPlacement.put("memo", name);
    expectedPlacement.put("positionAliasName", name);
    expectedPlacement.getJSONObject("site").put("pId", sitePid);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request =
        placementsRequest
            .getCreatePlacementRequest()
            .setRequestParams(requestMap)
            .setRequestPayload(expectedPlacement);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects site pid \"(.{1,20})\" for the company with pid \"(.{1,20})\"$")
  public void set_site_and_company_pid(String sitePid, String companyPid) {
    Site site = new Site();
    site.setPid(sitePid);
    commonSteps.setSite(site);

    Company company = new Company();
    commonSteps.companyPid = companyPid;
    company.setPid(companyPid);
    commonSteps.setCompany(company);
  }

  @When("^the user updates placement from the json file \"(.+?)\"$")
  public void the_user_updates_placement_from_the_json_file(String filename) throws Throwable {
    JSONObject expectedPlacement = JsonHandler.getJsonObjectFromFile(filename);
    String expectedPlacementId =
        databaseUtils.getPositionPidByName(expectedPlacement.getString("name"));
    expectedPlacement.put("pid", expectedPlacementId);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPlacementId(expectedPlacementId)
            .getRequestParams();
    commonSteps.request =
        placementsRequest
            .getUpdatePlacementRequest()
            .setRequestParams(requestMap)
            .setRequestPayload(expectedPlacement);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to create placement from the json file \"(.+?)\"$")
  public void unauthorized_user_tries_to_create_placement_from_the_json_file(String filename)
      throws Throwable {
    JSONObject expectedPlacement = JsonHandler.getJsonObjectFromFile(filename);
    String ANY_SELLER_PID = "000";
    String ANY_SITE_PID = "000";
    expectedPlacement.getJSONObject("site").put("pId", ANY_SITE_PID);
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(ANY_SELLER_PID)
            .setSitePid(ANY_SITE_PID)
            .getRequestParams();
    commonSteps.request =
        placementsRequest
            .getCreatePlacementRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPlacement);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to update placement from the json file \"(.+?)\"$")
  public void unauthorized_user_tries_to_update_placement_from_the_json_file(String filename)
      throws Throwable {
    JSONObject expectedPlacement = JsonHandler.getJsonObjectFromFile(filename);
    String ANY_SELLER_PID = "000";
    String ANY_SITE_PID = "000";
    String ANY_PLACEMENT_PID = "000";
    expectedPlacement.put("pid", ANY_PLACEMENT_PID);
    expectedPlacement.getJSONObject("site").put("pId", ANY_SITE_PID);
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(ANY_SELLER_PID)
            .setSitePid(ANY_SITE_PID)
            .setPlacementId(ANY_PLACEMENT_PID)
            .getRequestParams();
    commonSteps.request =
        placementsRequest
            .getUpdatePlacementRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPlacement);
    commonFunctions.executeRequest(commonSteps);
  }
}
