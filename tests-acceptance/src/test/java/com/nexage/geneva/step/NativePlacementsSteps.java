package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.request.NativePlacementsRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class NativePlacementsSteps {

  private static final int GENERATED_ID_LENGTH = 32;

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private NativePlacementsRequest nativePlacementsRequest;

  @When("^get native placement with pid \"([0-9]+)\"$")
  public void getNativePlacement(String pid) throws Throwable {
    Map<String, String> requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionId(pid)
            .getRequestParams();
    commonSteps.request = nativePlacementsRequest.getRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^create native placement \"(.+?)\"$")
  public void createNativePlacement(String fileName) throws Throwable {
    Map<String, String> requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();

    String payload = TestUtils.getResourceAsString(fileName);

    commonSteps.request =
        nativePlacementsRequest
            .createRequest()
            .setRequestParams(requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^update native placement \"(.+?)\"$")
  public void updateNativePlacement(String fileName) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(fileName);
    Map<String, String> requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionId(payload.getString("pid"))
            .getRequestParams();

    commonSteps.request =
        nativePlacementsRequest
            .updateRequest()
            .setRequestParams(requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("response will contain generated placement name")
  public void responseWillContainGeneratedPlacementName() throws Throwable {
    String name = commonSteps.serverResponse.object().getString("name");
    assertNotNull(name);
    assertEquals(GENERATED_ID_LENGTH, name.length());
  }
}
