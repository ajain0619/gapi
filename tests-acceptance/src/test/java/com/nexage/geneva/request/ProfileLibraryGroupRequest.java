package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileLibraryGroupRequest {

  @Autowired private Request request;

  public Request getGetLibraryTagRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/rtbprofilegroup/"
                + RequestParams.RTBPROFILE_GROUP_PID
                + "/hierarchy");
  }

  public Request getPostLibraryTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/applyGroups/");
  }
}
