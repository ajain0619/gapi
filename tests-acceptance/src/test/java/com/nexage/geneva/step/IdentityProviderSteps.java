package com.nexage.geneva.step;

import com.nexage.geneva.request.IdentityProviderRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class IdentityProviderSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;

  @Autowired private IdentityProviderRequests identityProviderRequests;

  @When("^the user searches for all identity providers$")
  public void the_user_searches_for_all_identity_providers() throws Throwable {
    commonSteps.request = identityProviderRequests.getGetAllIdentityProvidersRequest();
    commonFunctions.executeRequest(commonSteps);
  }
}
