package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.UserIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTORequests {

  @Autowired private Request request;

  public Request getPaginatedUsersForCompanyPidRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/users/?qf=%s&page=%s&qt=%s",
                RequestParams.QF, RequestParams.PAGE, RequestParams.QT));
  }

  public Request getPaginatedUsersForSellerSeatRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(String.format("/v1/users/?qf=%s&qt=%s", RequestParams.QF, RequestParams.QT));
  }

  public Request getPaginatedUsersList() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(String.format("/v1/users/?qf=%s&qt=%s", RequestParams.QF, RequestParams.QT));
  }

  public Request getUserForUserPidRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/users/" + RequestParams.USER_PID);
  }

  public Request getCreateUserRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectCreateUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.actualObjectCreateUser)
        .setUrlPattern("/v1/users");
  }

  public Request getUpdateUserRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectUpdateUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.expectedObjectUpdateUser)
        .setUrlPattern("/v1/users/" + RequestParams.USER_PID);
  }

  public Request getGetUsersV1Request() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/users?size=200")
        .setExpectedObjectIgnoredKeys(UserIgnoredKeys.expectedObjectSearchUser)
        .setActualObjectIgnoredKeys(UserIgnoredKeys.actualObjectSearchUser);
  }

  public Request getGetCurrentUserV1Request() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/users/?qf=onlyCurrent&qt=true");
  }
}
