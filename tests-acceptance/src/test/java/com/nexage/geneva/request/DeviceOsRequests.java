package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceOsRequests {

  private final String deviceTypeBaseUrl = "/v1/device-os";

  @Autowired private Request request;

  public Request getGetAllDeviceOsRequest() {
    return request.clear().setGetStrategy().setUrlPattern(deviceTypeBaseUrl);
  }

  public Request searchDeviceOs() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(deviceTypeBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
