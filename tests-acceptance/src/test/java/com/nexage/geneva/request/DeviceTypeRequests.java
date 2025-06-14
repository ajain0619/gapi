package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceTypeRequests {

  private final String deviceTypeBaseUrl = "/v1/device-types";

  @Autowired private Request request;

  public Request getGetAllDeviceTypesRequest() {
    return request.clear().setGetStrategy().setUrlPattern(deviceTypeBaseUrl);
  }

  public Request searchDeviceType() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(deviceTypeBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
