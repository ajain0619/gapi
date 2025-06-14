package com.nexage.geneva.step;

import com.nexage.geneva.request.AvailableAdSourcesRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class AvailableAdSourceSteps {

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private AvailableAdSourcesRequest availableAdSourcesRequest;

  @Given("^available ad sources are retrieved$")
  public void getAvailableAdSources() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        availableAdSourcesRequest
            .getAvailableAdSourcesRequest()
            .setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
