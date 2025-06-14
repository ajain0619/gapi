package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RtbProfileGroupRequests {

  @Autowired private Request request;

  public Request getCreateRequest() {
    return request.clear().setPostStrategy().setUrlPattern("/rtbprofilegroup");
  }

  public Request getGetRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/rtbprofilegroup/" + RequestParams.RTBPROFILE_GROUP_PID);
  }

  public Request getUpdateRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/rtbprofilegroup/" + RequestParams.RTBPROFILE_GROUP_PID);
  }

  public Request getDeleteRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/rtbprofilegroup/" + RequestParams.RTBPROFILE_GROUP_PID);
  }
}
