package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.nexage.geneva.config.properties.OIDCTokenRequestProperties;
import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.request.AccountServiceRequests;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.response.ResponseCode;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.OIDCTokenRequest;
import com.nexage.geneva.util.SetUpWiremockStubs;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class AccountServiceSteps {

  private static Logger logger = LoggerFactory.getLogger(AccountServiceSteps.class);

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private AccountServiceRequests accountServiceRequests;
  @Autowired private DatabaseUtils databaseUtils;

  public String username;
  @Autowired private OIDCTokenRequest oidcTokenRequest;

  @Autowired private OIDCTokenRequestProperties oidcTokenRequestProperties;

  public String password;
  public static String token;
  public Request request;

  @Given("^the user \"(.*?)\" has logged in$")
  public void the_user_logs_in_via_sso(String username) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockLogin(username);
    commonFunctions.removeAuthorizationValue();
    commonSteps.request = accountServiceRequests.getLoginRequestSSO();
    commonFunctions.executeSuccessfulSsoRequest(commonSteps);
    retrieveUserCompany(username);
  }

  @Given("^the user \"(.*?)\" has logged in with role \"(.*?)\"$")
  public void the_user_logs_in_via_sso(String username, String role) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockB2BLogin(username, role);
    commonFunctions.removeAuthorizationValue();
    commonSteps.request = accountServiceRequests.getLoginRequestSSO();
    commonFunctions.executeSuccessfulSsoRequest(commonSteps);
    retrieveUserCompany(username);
  }

  @Given("^setup wiremock user for provision API$")
  public void setup_wiremock_user_for_provision() throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockLogin(oidcTokenRequestProperties.getOnecentralUsername());
  }

  @Given("^the user \"([^\"]*)\" logs in via B2B$")
  public void the_user_logs_in_via_b2b(String username) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockB2BLogin(username);
    commonFunctions.setContentType("application/json");
    commonFunctions.setBearerAuthorizationHeader("hereIsMyToken");
  }

  @Given("^the user \"([^\"]*)\" logs in via B2B with role \"([^\"]*)\"$")
  public void the_user_logs_in_via_b2b(String username, String role) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockB2BLogin(username, role);
    commonFunctions.setContentType("application/json");
    commonFunctions.setBearerAuthorizationHeader("hereIsMyToken");
  }

  @Given("^the user \"(.*?)\" tries to log in$")
  public void the_user_tries_to_log_in_via_sso(String username) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockLogin(username);
    commonSteps.request = accountServiceRequests.getLoginRequestSSO();
    commonFunctions.executeFailedSsoRequest(commonSteps);
  }

  @Given("^the user fetches an authentication token$")
  public void the_user_gets_authentication_token() throws Throwable {
    token = oidcTokenRequest.getToken();
    assertNotNull("No token is returned", token);
    System.out.println("token returned is:" + token);
    commonFunctions.setContentType("application/json");
    commonFunctions.setBearerAuthorizationHeader(token);
  }

  @When("^current user details are retrieved with follow redirect is \"(.+?)\"$")
  public void current_user_details_are_retrieved(String redirect) throws Throwable {
    commonSteps.request =
        accountServiceRequests.getGetCurrentUserRequest(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the user logs out$")
  public void the_user_logs_out() throws Throwable {
    commonSteps.request = accountServiceRequests.getLogoutRequest();
    commonFunctions.removeAuthorizationValue();
    WireMock.reset();
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the \"(.+?)\" user with role \"(.+?)\" and options \"(.*?)\" has logged in$")
  public void the_user_with_role_and_options_has_logged_in(
      String affiliation, String role, String parameters) throws Throwable {
    String username = buildUsernameForMetadataTests(affiliation, role, parameters);
    setOneCentralUsernameForUser(username);
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockLogin(username);
    commonSteps.request = accountServiceRequests.getLoginRequestSSO();
    commonFunctions.executeSuccessfulSsoRequest(commonSteps);
  }

  @Then("^services are available with follow redirect is \"(.+?)\"")
  public void services_are_available(String redirect) throws Throwable {
    commonSteps.request =
        accountServiceRequests.getGetCurrentUserRequest(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);

    assertTrue(StringUtils.isEmpty(commonSteps.exceptionMessage), commonSteps.exceptionMessage);
    ResponseHandler.verifySuccessfulResponseCode(
        commonSteps.request, commonSteps.serverResponse, ResponseCode.OK);
  }

  @Then("^services are not available with follow redirect is \"(.+?)\"")
  public void services_are_not_available(String redirect) throws Throwable {
    commonSteps.request =
        accountServiceRequests.getGetCurrentUserRequest(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      ResponseHandler.verifyRedirectResponse(commonSteps.request, commonSteps.serverResponse);
    }
    assertTrue(commonSteps.exceptionMessage.isEmpty(), "Services are available.");
  }

  @Then("^the user \"(.+?)\" logs in$")
  public void the_user_logs_in(String username) throws Throwable {
    SetUpWiremockStubs setupwm = new SetUpWiremockStubs();
    setupwm.setUpWireMockLogin(username);
    commonSteps.request = accountServiceRequests.getLoginRequestSSO();
    commonFunctions.executeSuccessfulSsoRequest(commonSteps);
  }

  public static String getToken() {
    return token;
  }

  private String buildUsernameForMetadataTests(String affiliation, String role, String parameters) {
    Map<String, String> options = TestUtils.splitQuery(parameters);
    StringBuilder username = new StringBuilder();
    username.append("Cu");
    username.append(affiliation.replace(" ", ""));
    username.append(role.replace(" ", ""));
    for (String option : options.keySet()) {
      username.append(option.replace(" ", ""));
      username.append(options.get(option).replace(" ", ""));
    }
    return username.toString();
  }

  private void setOneCentralUsernameForUser(String username) {
    int updateCrudCore = databaseUtils.setOneCentralUsernameForUser(username);
    assertTrue(updateCrudCore == 0, "Crud Core could not be updated");
  }

  public void retrieveUserCompany(String oneCentralUsername) throws Throwable {
    commonSteps.companyPid = databaseUtils.getCompanyForOneCentralUserName(oneCentralUsername);
    commonSteps.setCompany(new Company());
    commonSteps.getCompany().setPid(commonSteps.companyPid);
  }
}
