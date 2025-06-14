package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SecurityRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class SecurityTestStep {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SecurityRequests securityRequests;
  private String companyPid;

  @Then("^the request returned status \"([^\"]*)\"$")
  public void is_request_authorized(HttpStatus status) throws Throwable {
    if (status == HttpStatus.OK) {
      commonSteps.request_passed_successfully();
    } else {
      commonSteps.failed_with_response_code(
          "Request to " + commonSteps.request.getUrl(), status.getCode());
    }
  }

  @And("^request to a company with pid \"([^\"]*)\"$")
  public void requestToACompanyWithPid(String companyPid) {
    this.companyPid = companyPid;
  }

  @When("^request to endpoint with role \"([^\"]*)\"$")
  public void requestToEndpointWithRole(String url) throws Throwable {
    commonSteps.request = securityRequests.getRequest(url);
    commonSteps.requestMap = new RequestParams().setPublisherPid(companyPid).getRequestParams();
    commonSteps.request = commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Getter
  public enum HttpStatus {
    OK("200"),
    UNAUTHORIZED("401"),
    FORBIDDEN("403");

    private final String code;

    HttpStatus(String code) {
      this.code = code;
    }
  }
}
