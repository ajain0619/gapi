package com.nexage.geneva.requeststrategy.impl;

import com.nexage.geneva.requeststrategy.RequestStrategy;
import com.nexage.geneva.rest.RestClient;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.monoid.web.JSONResource;

@Component
public class PutStrategy implements RequestStrategy {

  @Autowired private RestClient restClient;

  @Override
  public JSONResource executeRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable {
    return restClient.executePutRequest(url, payload, headers, redirect);
  }
}
