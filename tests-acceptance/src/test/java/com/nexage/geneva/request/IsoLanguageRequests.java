package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsoLanguageRequests {

  private final String isoLanguageBaseUrl = "/v1/iso-languages";

  @Autowired private Request request;

  public Request getGetAllIsoLanguagesRequest() {
    return request.clear().setGetStrategy().setUrlPattern(isoLanguageBaseUrl);
  }

  public Request searchIsoLanguage() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(isoLanguageBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
