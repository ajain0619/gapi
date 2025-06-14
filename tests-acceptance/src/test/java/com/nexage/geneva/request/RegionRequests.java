package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Describes regions requests */
@Component
public class RegionRequests {
  @Autowired private Request request;

  public Request getGetAllRegions() {
    return request.clear().setGetStrategy().setUrlPattern("/regions");
  }
}
