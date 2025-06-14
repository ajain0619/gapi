package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityProviderRequests {
  private final String identityProviderBaseUrl = "/v1/dsps/identity-providers";

  @Autowired private Request request;

  public Request getGetAllIdentityProvidersRequest() {
    return request.clear().setGetStrategy().setUrlPattern(identityProviderBaseUrl);
  }
}
