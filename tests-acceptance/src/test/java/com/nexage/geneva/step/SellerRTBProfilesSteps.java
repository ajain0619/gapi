package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellerRTBProfilesRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class SellerRTBProfilesSteps {
  public Map<String, String> requestMap = new HashMap<>();
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SellerRTBProfilesRequests rtbProfilesRequests;

  @Then("^the user grabs all RTBProfiles")
  public void the_user_grabs_all_RTBProfiles() throws Throwable {
    RequestParams requestParams = new RequestParams().setSellerPid("801");
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        rtbProfilesRequests.getRTBprofilesForSeller().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the user grabs all default RTBProfiles matching qt \"([^\"]*)\" with qf \"([^\"]*)\"$")
  public void get_all_RTBProfiles_matching_qt_with_qf(String qt, String qf) throws Throwable {
    RequestParams requestParams = new RequestParams().setSellerPid("802").setqt(qt).setqf(qf);
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        rtbProfilesRequests.getRTBprofilesFiltered().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a seller rtb profile from the json file \"(.+?)\"$")
  public void the_user_updates_an_seller_rtb_profile(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    requestMap = new RequestParams().setSellerPid("801").setRtbPid("60001").getRequestParams();
    commonSteps.request = rtbProfilesRequests.getUpdateRtbProfileRequest();
    commonSteps.request.setRequestParams(requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }
}
