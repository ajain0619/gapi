package com.nexage.geneva.step;

import com.nexage.geneva.model.crud.SellerAttributes;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellerAttributesRequest;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class SellerAttributesSteps {

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  @Autowired private SellerAttributesRequest sellerAttributesRequest;

  private Map<String, String> requestMap;

  @When("^the user updates seller attribute custom deal floor enabled to true$")
  public void updateCustomDealFloor() throws Throwable {

    commonSteps.request = sellerAttributesRequest.getUpdateSellerAttributes();
    requestMap = new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();

    SellerAttributes sa = new SellerAttributes();
    sa.setSellerPid(Long.valueOf(commonSteps.companyPid));
    sa.setVersion(1);
    sa.setHumanOptOut(false);
    sa.setSmartQPSEnabled(false);
    sa.setDefaultTransparencyMgmtEnablement("ENABLED");
    sa.setTransparencyMode("RealName");
    sa.setSellerNameAlias(null);
    sa.setSellerIdAlias(null);
    sa.setRevenueShare(BigDecimal.TEN);
    sa.setAdStrictApproval(false);
    sa.setRevenueGroupPid(1L);
    sa.setCustomDealFloorEnabled(true);
    JSONObject payload = new JSONObject(TestUtils.convertToJson(sa));

    commonSteps.request.setRequestParams(requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates seller attributes prebid and postbid human sampling rates to \"(.+)\" and \"(.+)\" respectively$")
  public void updateSellerAttributesHumanSamplingRates(String prebidRate, String postbidRate)
      throws Throwable {

    commonSteps.request = sellerAttributesRequest.getUpdateSellerAttributes();
    requestMap = new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();

    SellerAttributes sa = new SellerAttributes();
    sa.setSellerPid(Long.valueOf(commonSteps.companyPid));
    sa.setVersion(1);
    sa.setHumanOptOut(false);
    sa.setSmartQPSEnabled(false);
    sa.setDefaultTransparencyMgmtEnablement("ENABLED");
    sa.setTransparencyMode("RealName");
    sa.setSellerNameAlias(null);
    sa.setSellerIdAlias(null);
    sa.setRevenueShare(BigDecimal.TEN);
    sa.setAdStrictApproval(false);
    sa.setRevenueGroupPid(1L);
    sa.setHumanPrebidSampleRate(Integer.valueOf(prebidRate));
    sa.setHumanPostbidSampleRate(Integer.valueOf(postbidRate));
    JSONObject payload = new JSONObject(TestUtils.convertToJson(sa));

    commonSteps.request.setRequestParams(requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets seller attributes$")
  public void getSellerAttributes() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSellerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        sellerAttributesRequest.getSellerAttributes().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets seller attributes for seller id \"(.+?)\"$")
  public void getSellerAttributes(String sellerPid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setSellerPid(sellerPid).getRequestParams();
    commonSteps.request =
        sellerAttributesRequest.getSellerAttributes().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
