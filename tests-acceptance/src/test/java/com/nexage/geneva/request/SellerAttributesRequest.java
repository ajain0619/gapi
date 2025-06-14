package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerAttributesRequest {

  @Autowired private Request request;

  public Request getUpdateSellerAttributes() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID + "/seller-attributes");
  }

  public Request getSellerAttributes() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID + "/seller-attributes");
  }
}
