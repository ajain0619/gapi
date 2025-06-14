package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoohScreenRequest {

  @Autowired Request request;

  public Request createDoohScreenWithFile() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID + "/screens");
  }

  public Request createDoohScreenGetRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID + "/screens");
  }
}
