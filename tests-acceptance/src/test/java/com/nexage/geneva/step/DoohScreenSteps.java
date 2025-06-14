package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.DoohScreenRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.web.FormData;
import us.monoid.web.Resty;

public class DoohScreenSteps {

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private DoohScreenRequest doohScreenRequest;

  @Autowired private DatabaseUtils databaseUtils;

  @When(
      "^the user creates screens for seller \"(.+)\" from JSON file sending as form data \"(.+)\"$")
  public void createMultipartScreensFile(String sellerPid, String fileName) throws Throwable {
    commonSteps.request =
        doohScreenRequest
            .createDoohScreenWithFile()
            .setRequestParams(new RequestParams().setSellerPid(sellerPid).getRequestParams())
            .setRequestPayload(
                new FormData(
                    "screens",
                    "screens",
                    Resty.content(JsonHandler.getJsonArrayFromFile(fileName))));
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user reads screens for seller \"(.+)\"$")
  public void getDoohScreens(String sellerPid) throws Throwable {
    commonSteps.request =
        doohScreenRequest
            .createDoohScreenGetRequest()
            .setRequestParams(new RequestParams().setSellerPid(sellerPid).getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^database contains the following ssp screen ids \"(.+)\"$")
  public void verifyDatabaseContainsSspScreenIds(String sspScreenIds) {
    boolean sspScreenIdsExist =
        databaseUtils.sspScreenIdsExist(Arrays.asList(sspScreenIds.split(",")));
    assertTrue(sspScreenIdsExist, "Not all ssp screen ids exist");
  }

  @When("^venue is initialized")
  public void initializeVenueTypes() {
    databaseUtils.initializeVenueTypes();
  }
}
