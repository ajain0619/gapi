package com.nexage.geneva.step;

import com.nexage.geneva.request.EntitlementsRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

public class EntitlementsSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private EntitlementsRequest entitlementsRequest;

  @Then(
      "^the user grabs all entitlements matching qt (.+?) with qf (.+?) with follow redirect is \"(.+?)\"$")
  public void get_all_entitlements_qt_with_qf(String qt, String qf, String redirect)
      throws Throwable {
    RequestParams requestParams = new RequestParams().setqt(qt).setqf(qf);
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        entitlementsRequest
            .getUserEntitlementsFiltered()
            .setRequestParams(commonSteps.requestMap)
            .setFollowRedirects(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);
  }
}
