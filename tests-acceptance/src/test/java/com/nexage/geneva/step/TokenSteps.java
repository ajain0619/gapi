package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.TokenRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private TokenRequests tokenRequests;

  @Then(
      "^the user grabs all tokens matching qt (.+?) with qf (.+?) with follow redirect is \"(.+?)\"$")
  public void get_all_tokens_qt_with_qf(String qt, String qf, String redirect) throws Throwable {
    RequestParams requestParams = new RequestParams().setqt(qt).setqf(qf);
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        tokenRequests
            .getTokensFiltered(Boolean.valueOf(redirect))
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
