package com.nexage.geneva.request;

import static com.nexage.geneva.request.RequestParams.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerInventoryAttributeValuesRequests {

  private static final String LIST_REQUEST_PATTERN =
      "/v1/sellers/"
          + SELLER_PID
          + "/inventory-attributes/"
          + ATTRIBUTE_PID
          + "/inventory-attribute-values";

  private static final String INDIVIDUAL_REQUEST_PATTERN =
      "/v1/sellers/"
          + SELLER_PID
          + "/inventory-attributes/"
          + ATTRIBUTE_PID
          + "/inventory-attribute-values/"
          + ATTRIBUTE_VALUE_PID;

  @Autowired private Request request;

  public Request createInventoryAtributeValueForSeller() {
    return request.clear().setPostStrategy().setUrlPattern(LIST_REQUEST_PATTERN);
  }

  public Request getAllInventoryAttributeValues() {
    return request.clear().setGetStrategy().setUrlPattern(LIST_REQUEST_PATTERN);
  }

  public Request putInventoryAttributeValue() {
    return request.clear().setPutStrategy().setUrlPattern(INDIVIDUAL_REQUEST_PATTERN);
  }
}
