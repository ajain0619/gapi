package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.UserIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRequests {

  @Autowired private Request request;

  public Request getCreateUserRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectCreateUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.actualObjectCreateUser)
        .setUrlPattern("/users");
  }

  public Request getGetUsersRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/users")
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectSearchUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.actualObjectSearchUser);
  }

  public Request getUpdateUserRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectUpdateUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.actualObjectUpdateUser)
        .setUrlPattern("/users/" + RequestParams.USER_PID);
  }

  public Request getDeleteUserRequest() {
    return request.clear().setDeleteStrategy().setUrlPattern("/users/" + RequestParams.USER_PID);
  }

  public Request getGetUsersByCompanyRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/users?companyPID=" + RequestParams.COMPANY_PID);
  }

  public Request getGetAllowedSitesForUserRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers/sitesummaries?userPID=" + RequestParams.USER_PID);
  }

  public Request getGetUsersByPidRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/users/" + RequestParams.USER_PID);
  }

  public Request getResetUserPasswordRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/users/" + RequestParams.USER_PID + "/resetPasswd");
  }

  public Request getChangeUserPasswordRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/users/"
                + RequestParams.USER_PID
                + "/changePasswd"
                + "?oldPass="
                + RequestParams.OLD_PASSWORD
                + "&newPass="
                + RequestParams.NEW_PASSWORD);
  }

  public Request getRestrictAccessToSiteRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/users/"
                + RequestParams.USER_PID
                + "/restrictAccessToSite?sitePID="
                + RequestParams.SITE_PID);
  }

  public Request getAllowAccessToSiteRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/users/"
                + RequestParams.USER_PID
                + "/allowAccessToSite?sitePID="
                + RequestParams.SITE_PID);
  }
}
