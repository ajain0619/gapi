package com.nexage.geneva.step;

import com.nexage.geneva.request.StaticRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class StaticSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private StaticRequests staticRequests;

  @Given(
      "^a request is made to the endpoint to read the static resource with the filename \"(.+)\"$")
  public void retrieve_static_resource(final String fileName) throws Throwable {
    commonSteps.request = staticRequests.getStaticResourceRequest(fileName);
    commonFunctions.executeRequest(commonSteps);
  }
}
