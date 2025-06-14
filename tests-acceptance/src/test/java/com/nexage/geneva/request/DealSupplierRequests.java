package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by seanryan on 24/02/2016. */
@Component
public class DealSupplierRequests {

  @Autowired private Request request;

  public Request getGetSupplierRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/deals/suppliers/" + RequestParams.PROFILE_PID);
  }

  public Request getGetAllSuppliersRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/deals/suppliers");
  }
}
