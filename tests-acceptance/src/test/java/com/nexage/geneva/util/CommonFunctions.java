package com.nexage.geneva.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.rest.impl.RestyHelper;
import com.nexage.geneva.step.CommonSteps;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.monoid.json.JSONArray;
import us.monoid.web.JSONResource;

@Component
@Log4j2
public class CommonFunctions {
  @Autowired private RestyHelper restyHelper;

  public Map<String, Company> getCompanies(CommonSteps commonSteps) throws Throwable {
    JSONArray companiesArray = commonSteps.serverResponse.array();
    assertNotNull(companiesArray, commonSteps.companyType.getName() + " companies not found");

    Map<String, Company> companyMap = new HashMap<>();
    for (int i = 0; i < companiesArray.length(); i++) {
      Company company = TestUtils.mapper.readValue(companiesArray.get(i).toString(), Company.class);
      companyMap.put(company.getName(), company);
    }
    return companyMap;
  }

  public Map<String, Site> getSites(JSONResource serverResponse) throws Throwable {
    JSONArray sellerSitesArray = serverResponse.array();
    assertNotNull(sellerSitesArray, "Seller site are not found");

    Map<String, Site> siteMap = new HashMap<>();
    for (int i = 0; i < sellerSitesArray.length(); i++) {
      Site site = TestUtils.mapper.readValue(sellerSitesArray.get(i).toString(), Site.class);
      siteMap.put(site.getName(), site);
    }
    return siteMap;
  }

  public void executeRequest(CommonSteps commonSteps) throws Throwable {
    commonSteps.exceptionMessage = "";
    commonSteps.serverResponse = null;
    try {
      commonSteps.serverResponse = commonSteps.request.execute();
    } catch (IOException e) {
      commonSteps.exceptionMessage = e.getMessage();
    }
  }

  public void executeSuccessfulSsoRequest(CommonSteps commonSteps) throws Throwable {
    String baseUri = commonSteps.request.executeSsoLogin();
    log.info("SSO base URL: {}", baseUri);
    assertTrue(
        baseUri.contains("login=success") || baseUri.contains("/actuator/info"),
        "sso login failed");
  }

  public void executeFailedSsoRequest(CommonSteps commonSteps) throws Throwable {
    String baseUri = commonSteps.request.executeSsoLogin();
    assertTrue(
        baseUri.contains("login=failed") || baseUri.contains("/unauthorized"),
        "sso login succeeded but was expected to fail");
  }

  public void removeAuthorizationValue() {
    restyHelper.removeAuthorizationValue();
  }

  public void setContentType(String contentType) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", contentType);
    restyHelper.addHeaders(headers);
  }

  public void setBearerAuthorizationHeader(String token) {
    restyHelper.setBearerAuthorizationHeader(token);
  }
}
