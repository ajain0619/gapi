package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SearchRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchTestSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SearchRequests searchRequests;

  @When("^performs search with multi value search query \"([^\"]*)\" and operator \"([^\"]*)\"$")
  public void performsSearchWithMultiValueSearchQuery(String qf, String qo) throws Throwable {
    assertNotNull(qf);
    assertNotNull(qo);
    RequestParams requestParams = new RequestParams();
    Request request = searchRequests.getRequestWithQf();
    requestParams.setqf(qf);
    if (!qo.isEmpty()) {
      requestParams.setqo(qo);
      request = searchRequests.getRequestWithQfAndQo();
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request = request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
