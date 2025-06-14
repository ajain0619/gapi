package com.nexage.geneva.request;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** <code>SellingRuleRequests</code> */
@Component
public class SellingRuleRequests {
  @Autowired private Request request;

  private final String ruleCreateUrl = "/pss/{companyPid}/rule";
  private final String ruleGetAllUrl = "/pss/{companyPid}/rule?type={ruleType}";
  private final String ruleGetUpdateDeleteUrl = "/pss/{companyPid}/rule/{rulePid}/";

  private final String ruleCreateGetAllWithoutPublisherUrl = "/pss/rule";
  private final String ruleGetUpdateDeleteWithoutPublisherUrl = "/pss/rule/{rulePid}/";
  private final String getRulesAssociatedWithDealUrl = "/deals/{dealPid}/rule";

  private final String sellerRulesBaseUrl = "/v1/sellers/{companyPid}/rules/";
  private final String sellerCreateRuleUrl = sellerRulesBaseUrl;
  private final String getUpdateDeleteRuleUrl = sellerRulesBaseUrl + "{rulePid}";

  private final String experimentRuleCreateUrl = "/v1/rules";
  private final String experimentRuleUpdateUrl = "/v1/rules/{rulePid}";
  private final String experimentRuleReadUrl = "/v1/rules?experimentPid={experimentPid}";

  /** @deprecated use {@link SellingRuleRequests#getV1CreateRuleRequest() instead} */
  @Deprecated
  public Request getCreateRuleRequest() {
    return request.clear().setPostStrategy().setUrlPattern(ruleCreateUrl);
  }

  public Request getV1CreateRuleRequest() {
    return request.clear().setPostStrategy().setUrlPattern(sellerCreateRuleUrl);
  }

  public Request getGetAllRulesRequest() {
    return request.clear().setGetStrategy().setUrlPattern(ruleGetAllUrl);
  }

  public Request getGetOneRuleForSellerRequest() {
    return request.clear().setGetStrategy().setUrlPattern(getUpdateDeleteRuleUrl);
  }

  public Request getV1UpdateRuleRequest() {
    return request.clear().setPutStrategy().setUrlPattern(getUpdateDeleteRuleUrl);
  }

  public Request getGetRulesForSellerRequest() {
    String urlPattern = sellerRulesBaseUrl.concat("?qf={qf}&qo={qo}");
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(urlPattern);
  }

  public Request getGetRulesForSellerByTypeRequest() {
    String urlPattern = sellerRulesBaseUrl.concat("?type=" + RequestParams.RULE_TYPE);
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(urlPattern);
  }

  public Request getGetRulesWithoutCriteriaForSellerRequest() {
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(sellerRulesBaseUrl);
  }

  public Request getDeleteRuleForSellerRequest() {
    return request.clear().setDeleteStrategy().setUrlPattern(getUpdateDeleteRuleUrl);
  }

  public Request getCreateRuleWithoutPublisher() {
    return request.clear().setPostStrategy().setUrlPattern(ruleCreateGetAllWithoutPublisherUrl);
  }

  public Request getGetAllRulesWithoutPublisher() {
    return request.clear().setGetStrategy().setUrlPattern(ruleCreateGetAllWithoutPublisherUrl);
  }

  public Request getGetOneRuleWithoutPublisherRequest() {
    return request.clear().setGetStrategy().setUrlPattern(ruleGetUpdateDeleteWithoutPublisherUrl);
  }

  public Request getUpdateRuleWithoutPublisherRequest() {
    return request.clear().setPutStrategy().setUrlPattern(ruleGetUpdateDeleteWithoutPublisherUrl);
  }

  public Request getDeleteRuleWithoutPublisherRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(ruleGetUpdateDeleteWithoutPublisherUrl);
  }

  public Request getRulesAssociatedWithDealRequest() {
    return request.clear().setGetStrategy().setUrlPattern(getRulesAssociatedWithDealUrl);
  }

  public Request getPlacementFormulaGrid() {
    return request.clear().setPostStrategy().setUrlPattern("/v1/formula-inventories/{companyPid}");
  }

  public Request getPlacementFormulaGridForDeals() {
    return request.clear().setPostStrategy().setUrlPattern("/v1/formula-inventories");
  }

  public Request getExperimentRuleCreateRequest() {
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Content-Type", "application/vnd.geneva-api.experiment-rule+json");
    requestHeaders.put("Accept", "application/vnd.geneva-api.experiment-rule+json");
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(experimentRuleCreateUrl)
        .setRequestHeaders(requestHeaders);
  }

  public Request getExperimentRuleUpdateRequest() {
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Content-Type", "application/vnd.geneva-api.experiment-rule+json");
    requestHeaders.put("Accept", "application/vnd.geneva-api.experiment-rule+json");
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(experimentRuleUpdateUrl)
        .setRequestHeaders(requestHeaders);
  }

  public Request getExperimentRuleReadRequest() {
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Accept", "application/vnd.geneva-api.experiment-rule+json");
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(experimentRuleReadUrl)
        .setRequestHeaders(requestHeaders);
  }
}
