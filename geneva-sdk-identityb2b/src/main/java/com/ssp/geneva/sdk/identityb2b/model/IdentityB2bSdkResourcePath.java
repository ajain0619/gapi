package com.ssp.geneva.sdk.identityb2b.model;

import lombok.Getter;

@Getter
public enum IdentityB2bSdkResourcePath {
  ACCESS_TOKEN_URL_PATH("/identity/oauth2/access_token"),
  USER_INFO_URL_PATH("/identity/oauth2/userinfo");
  private final String resourcePath;

  IdentityB2bSdkResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }
}
