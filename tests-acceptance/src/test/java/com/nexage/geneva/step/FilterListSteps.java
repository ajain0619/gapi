package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.FilterListRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class FilterListSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private FilterListRequests filterListRequests;
  @Autowired private DatabaseUtils databaseUtils;

  @When("^the user gets all filter lists with buyer id \"(.*?)\"$")
  public void the_user_gets_filter_lists(String buyerId) throws Throwable {
    commonSteps.requestMap = new RequestParams().setBuyerPid(buyerId).getRequestParams();

    commonSteps.request =
        filterListRequests.getGetFilterListsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets filter list with id \"(.*?)\"$")
  public void the_user_gets_filter_list(String filterListId) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setFilterListId(filterListId)
            .getRequestParams();
    commonSteps.request =
        filterListRequests.getGetFilterListRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates filter list from the json file \"(.*?)\"$")
  public void the_user_creates_filter_list_from_json_file(String filename) throws Throwable {
    JSONObject expectedFilterList = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        filterListRequests
            .getCreateFilterListRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedFilterList);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes filter list with id \"(.*?)\"$")
  public void the_user_deletes_filter_list(String filterListId) throws Throwable {

    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setFilterListId(filterListId)
            .getRequestParams();
    commonSteps.request =
        filterListRequests.getDeleteFilterListRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets domains of a filter list with id \"(.*?)\"$")
  public void the_user_gets_filter_list_domains(String filterListId) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setFilterListId(filterListId)
            .getRequestParams();
    commonSteps.request =
        filterListRequests
            .getGetFilterListDomainsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
