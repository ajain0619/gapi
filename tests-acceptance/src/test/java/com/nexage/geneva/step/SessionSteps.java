package com.nexage.geneva.step;

import com.nexage.geneva.request.SessionRequest;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionSteps {
  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private SessionRequest sessionRequest;

  @When("^the user deletes the session")
  public void deleteSession() throws Throwable {
    commonSteps.request = sessionRequest.deleteSession();

    commonFunctions.executeRequest(commonSteps);
  }
}
