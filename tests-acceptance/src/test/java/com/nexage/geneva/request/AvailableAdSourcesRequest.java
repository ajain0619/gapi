package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.AvailableAdSourceIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvailableAdSourcesRequest {

  @Autowired private Request request;

  public Request getAvailableAdSourcesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(AvailableAdSourceIgnoredKeys.actualAndExpectedObjectGet)
        .setExpectedObjectIgnoredKeys(AvailableAdSourceIgnoredKeys.actualAndExpectedObjectGet)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/availableadsources");
  }
}
