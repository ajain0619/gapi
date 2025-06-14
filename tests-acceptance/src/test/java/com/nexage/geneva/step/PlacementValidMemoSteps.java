package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.request.PlacementValidMemoRequest;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.web.JSONResource;

public class PlacementValidMemoSteps {
  @Autowired private PlacementValidMemoRequest placementValidMemoRequest;

  public Map<String, String> requestMap;
  public Request request;

  private String companyId;
  private String siteId;
  private JSONResource resp;

  @When("^the user selects the company \"(.*)\" and site \"(.*)\"")
  public void selectCompany(String companyId, String siteId) throws Throwable {
    this.companyId = companyId;
    this.siteId = siteId;
  }

  @When("^the user copies the placement with memo \"(.*)\"$")
  public void copyPlacement(String placementMemo) throws Throwable {
    this.requestMap =
        new RequestParams()
            .setCompanyPid(companyId)
            .setSitePid(siteId)
            .setPlacementMemo(placementMemo)
            .getRequestParams();
    request = placementValidMemoRequest.getPlacementValidMemoRequest().setRequestParams(requestMap);
    resp = request.execute();
  }

  @Then("^the new placement memo starts with \"(.*)\"$")
  public void validateMemo(String pattern) throws Throwable {
    assertTrue(((String) resp.get("validMemo")).matches(pattern));
  }

  @Then("^the new placement memo is empty$")
  public void validateMemo() throws Throwable {
    assertTrue(((String) resp.get("validMemo")).isEmpty());
  }

  @Then("^the new placement memo is unique$")
  public void validateIsUnique() throws Throwable {
    assertTrue((Boolean) resp.get("unique"));
  }

  @Then("^the new placement memo is not unique$")
  public void validateIsNotUnique() throws Throwable {
    assertFalse((Boolean) resp.get("unique"));
  }
}
