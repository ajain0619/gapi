package com.nexage.geneva.step;

import com.nexage.geneva.request.RegionRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/** Created by a.vnuchko on 22.03.2018. */
public class RegionsSteps {
  @Autowired private RegionRequests regionRequests;
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;

  @When("^the user gets all available regions$")
  public void the_user_creates_a_company_from_the_json_file() throws Throwable {
    commonSteps.request = regionRequests.getGetAllRegions();
    commonFunctions.executeRequest(commonSteps);
  }
}
