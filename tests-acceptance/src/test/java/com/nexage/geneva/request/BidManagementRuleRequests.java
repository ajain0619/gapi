package com.nexage.geneva.request;

import com.nexage.geneva.util.TestUtils;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BidManagementRuleRequests {

  @Autowired private Request request;

  public static final String BID_MANAGEMENT_HEADER =
      "application/vnd.geneva-api.bid-management-rule+json";

  private static final String ruleCreateUrl = "/v1/rules";
  private static final String ruleGetBySellerUrl = "/v1/sellers/";
  private static final String ruleGetByPlacementUrl = "/v1/placements/";
  private static final String ruleGetByRulePidsUrl = "/v1/rules?pids=";
  private static final String ruleUpdateUrl = "/v1/rules/";
  private static final String ruleGetByPid = "/v1/sellers/{companyPid}/rules/{rulePid}";

  private static final String v1rulesBaseUrl = "/v1/sellers/{companyPid}/rules";
  private static final String v1rulesUpdateUrl = "/v1/sellers/{companyPid}/rules/{rulePid}";

  private final Map<String, String> bmHeadersAcceptOnly =
      Map.of(HttpHeaders.ACCEPT, BID_MANAGEMENT_HEADER);
  private final Map<String, String> bmHeadersBoth =
      Map.of(
          HttpHeaders.ACCEPT,
          BID_MANAGEMENT_HEADER,
          HttpHeaders.CONTENT_TYPE,
          BID_MANAGEMENT_HEADER);

  /**
   * @deprecated Use {@link #createBidManagementRuleUsingV1Endpoint(String)} instead.
   * @param payloadFileName JSON payload file path
   * @return request ready for execution
   * @throws Throwable thrown when error occurs while reading <code>payloadFileName</code> file
   */
  @Deprecated
  public Request createBidManagementRule(String payloadFileName) throws Throwable {
    return request
        .clear()
        .setPostStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(ruleCreateUrl)
        .setRequestPayload(TestUtils.getResourceAsString(payloadFileName));
  }

  /**
   * Produces {@link Request} that can be used to create bid management rule using external API
   * endpoint.
   *
   * @param payloadFileName JSON payload file path
   * @return request ready for execution
   * @throws Throwable thrown when error occurs while reading <code>payloadFileName</code> file
   */
  public Request createBidManagementRuleUsingV1Endpoint(String payloadFileName) throws Throwable {
    return request
        .clear()
        .setPostStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(v1rulesBaseUrl)
        .setRequestPayload(TestUtils.getResourceAsString(payloadFileName));
  }

  public Request getBidManagementRuleByPublisherId() {
    return request
        .clear()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(ruleGetBySellerUrl + RequestParams.PUBLISHER_PID + "/rules");
  }

  public Request getBidManagementRuleByPlacementId() {
    return request
        .clear()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(ruleGetByPlacementUrl + RequestParams.PLACEMENT_ID + "/rules");
  }

  public Request getBidManagementRulesByRuleIds() {
    return request
        .clear()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(ruleGetByRulePidsUrl + RequestParams.RULE_PIDS);
  }

  public Request updateBidManagementRule(String payLoadString) {
    return request
        .clear()
        .setPutStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(ruleUpdateUrl + RequestParams.RULE_PID)
        .setRequestPayload(payLoadString);
  }

  public Request updateBidManagementRuleUsingV1Endpoint(String payLoadString) {
    return request
        .clear()
        .setPutStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(v1rulesUpdateUrl)
        .setRequestPayload(payLoadString);
  }

  public Request getBidManagementRuleByPid() {
    return request
        .clear()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersAcceptOnly)
        .setUrlPattern(ruleGetByPid);
  }

  /**
   * Produces {@link Request} that can be used to search for bid management rule by query field
   * parameter using external API endpoint.
   *
   * @return request ready for execution
   */
  public Request getGetBMRulesForSellerRequest() {
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(v1rulesBaseUrl.concat("?qf={qf}&qo={qo}"));
  }

  /**
   * Produces {@link Request} that can be used to search for bid management rule without query field
   * parameter using external API endpoint.
   *
   * @return request ready for execution
   */
  public Request getGetBMRulesWithoutCriteriaForSellerRequest() {
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setRequestHeaders(bmHeadersBoth)
        .setUrlPattern(v1rulesBaseUrl);
  }
}
