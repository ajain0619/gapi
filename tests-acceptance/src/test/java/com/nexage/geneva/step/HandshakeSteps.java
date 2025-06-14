package com.nexage.geneva.step;

import com.nexage.geneva.request.HandshakeRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class HandshakeSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private HandshakeRequests handshakeRequests;

  private String queryParameters;

  @Given("^the query parameters are \"(.+?)\"$")
  public void the_query_parameters_are(String queryParameters) throws Throwable {
    this.queryParameters = queryParameters;
  }

  @When("^the user does a call to retrieve all the existing handshake keys$")
  public void the_user_does_a_call_to_retrieve_all_the_existing_handshake_keys() throws Throwable {
    commonSteps.request = handshakeRequests.getGetAllExistingHandshakeKeysRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user does a call to retrieve a handshake config by pid \"(.+?)\"$")
  public void the_user_does_a_call_to_retrieve_a_handshake_config_by_pid(String handshakePid)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setHandshakePid(handshakePid).getRequestParams();
    commonSteps.request =
        handshakeRequests.getGetHandshakeConfigRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user does a call to delete a handshake config by pid \"(.+?)\"$")
  public void the_user_does_a_call_to_delete_a_handshake_config_by_pid(String handshakePid)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setHandshakePid(handshakePid).getRequestParams();
    commonSteps.request =
        handshakeRequests
            .getDeleteHandshakeConfigRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user does a call to update handshake config from the json file \"(.+?)\" on pid \"(.+?)\"$")
  public void the_user_does_a_call_to_update_handshake_config_from_the_json_file(
      String filename, String handshakePid) throws Throwable {
    JSONObject handshakeChanges = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap = new RequestParams().setHandshakePid(handshakePid).getRequestParams();
    commonSteps.request =
        handshakeRequests
            .getUpdateHandshakeConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(handshakeChanges);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user does a call to create handshake from the json file \"(.+?)\"$")
  public void the_user_does_a_call_to_create_handshake_from_the_json_file(String filename)
      throws Throwable {
    JSONObject handshakeCreate = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap =
        new RequestParams().setHandshakeParams(queryParameters).getRequestParams();
    commonSteps.request =
        handshakeRequests
            .getCreateHandshakeConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(handshakeCreate);
    commonFunctions.executeRequest(commonSteps);
  }
}
