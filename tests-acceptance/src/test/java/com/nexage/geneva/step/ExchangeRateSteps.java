package com.nexage.geneva.step;

import com.nexage.geneva.request.ExchangeRateRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class ExchangeRateSteps {

  @Autowired private CommonFunctions commonFunctions;
  @Autowired private CommonSteps commonSteps;
  @Autowired private ExchangeRateRequests exchangeRateRequests;

  @When("^the user gets exchange rate with qf \"(.*?)\" and qt \"(.*?)\" and latest \"(.*?)\"$")
  public void the_user_gets_exchange_rates_with_qf_qt_latest(String qf, String qt, String latest)
      throws Throwable {
    RequestParams requestParams = new RequestParams();
    if (!qf.equals("null")) {
      requestParams.setqf(qf);
    }
    if (!qt.equals("null")) {
      requestParams.setqt(qt);
    }
    if (!latest.equals("null")) {
      requestParams.setLatest(latest);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        exchangeRateRequests
            .getExchangeRatesWithQfAndQtAndLatest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
