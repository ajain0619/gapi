package com.ssp.geneva.sdk.onecentral.model;

import lombok.Getter;

@Getter
public enum OneCentralSdkResourcePath {
  CREATE_SSP_USER_URL_PATH("/one-central/user-migration/v1/users"),
  CREATE_USER_URL_PATH("/one-central/user-management/v6/users/"),
  CREATE_API_USER_URL_PATH("/one-central/user-management/v6/users/"),
  UPDATE_USER_URL_PATH("/one-central/user-management/v6/users/{username}"),
  RESET_PASSWORD_URL_PATH("/one-central/user-management/v6/users/{username}/password"),
  ROLE_ASSIGN_URL_PATH(
      "/one-central/authorization-management/v2/entities/{type}/{id}/roles/{roleId}"),
  ROLE_FETCH_URL_PATH("/one-central/authorization-management/v2/entities/{type}/{id}/roles"),
  GET_USERS("/one-central/user-management/v6/users"),
  GET_SINGLE_USER_URL_PATH("/one-central/user-management/v6/users/{username}"),
  GET_USER_AUTH_URL_PATH("/one-central/user-authorization/v3/users/authorization");

  private final String resourcePath;

  OneCentralSdkResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }
}
