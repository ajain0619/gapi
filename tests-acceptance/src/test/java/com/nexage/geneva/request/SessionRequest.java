package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionRequest {
  @Autowired private Request request;

  public Request deleteSession() {
    return request.clear().setDeleteStrategy().setUrlPattern("/v1/sessions/");
  }
}
