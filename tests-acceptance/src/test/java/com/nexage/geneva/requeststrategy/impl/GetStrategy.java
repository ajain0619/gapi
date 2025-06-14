package com.nexage.geneva.requeststrategy.impl;

import com.nexage.geneva.requeststrategy.RequestStrategy;
import com.nexage.geneva.rest.RestClient;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.monoid.web.JSONResource;

@Component
public class GetStrategy implements RequestStrategy {

  @Autowired private RestClient restClient;

  @Override
  public JSONResource executeRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable {
    return restClient.executeGetRequest(url, headers, redirect);
  }

  @Override
  public void authenticate(String url, String login, String password) {
    restClient.authenticate(url, login, password);
  }
}
