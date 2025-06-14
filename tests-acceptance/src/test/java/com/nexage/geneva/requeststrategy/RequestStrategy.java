package com.nexage.geneva.requeststrategy;

import java.util.Map;
import us.monoid.web.JSONResource;

public interface RequestStrategy {

  JSONResource executeRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable;

  default void authenticate(String url, String login, String password) {}
}
