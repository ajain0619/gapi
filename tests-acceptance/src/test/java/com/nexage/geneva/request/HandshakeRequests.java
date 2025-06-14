package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.HandshakeIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandshakeRequests {

  @Autowired private Request request;

  public Request getGetAllExistingHandshakeKeysRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/sdkhandshake");
  }

  public Request getGetHandshakeConfigRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sdkhandshake/" + RequestParams.HANDSHAKE_PID);
  }

  public Request getDeleteHandshakeConfigRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/sdkhandshake/" + RequestParams.HANDSHAKE_PID);
  }

  public Request getUpdateHandshakeConfigRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/sdkhandshake/" + RequestParams.HANDSHAKE_PID);
  }

  public Request getCreateHandshakeConfigRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/sdkhandshake?" + RequestParams.HANDSHAKE_PARAMS)
        .setActualObjectIgnoredKeys(HandshakeIgnoredKeys.handshakeCreate);
  }
}
