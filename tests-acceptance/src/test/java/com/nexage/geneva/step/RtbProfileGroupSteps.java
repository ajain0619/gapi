package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.RtbProfileGroupRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class RtbProfileGroupSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private RtbProfileGroupRequests rtbProfileGroupRequests;

  private JSONObject payload;

  @When("^the user creates an rtb profile group from the json file \"(.+?)\"$")
  public void the_user_creates_an_rtb_profile_group_from_the_json_file(String filePath)
      throws Throwable {
    payload = JsonHandler.getJsonObjectFromFile(filePath);
    commonSteps.request = rtbProfileGroupRequests.getCreateRequest().setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets the rtb profile group with pid \"(.+?)\"$")
  public void the_user_gets_the_rtb_profile_group_with_pid(String groupPid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setGroupPid(groupPid).getRequestParams();
    commonSteps.request =
        rtbProfileGroupRequests.getGetRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the rtb profile group with pid \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_the_rtb_profile_group_with_pid(String groupPid, String filePath)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setGroupPid(groupPid).getRequestParams();
    payload = JsonHandler.getJsonObjectFromFile(filePath);
    commonSteps.request =
        rtbProfileGroupRequests
            .getUpdateRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the rtb profile group with pid \"(.+?)\"$")
  public void the_user_deletes_the_rtb_profile_group_with_pid(String groupPid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setGroupPid(groupPid).getRequestParams();
    commonSteps.request =
        rtbProfileGroupRequests.getDeleteRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
