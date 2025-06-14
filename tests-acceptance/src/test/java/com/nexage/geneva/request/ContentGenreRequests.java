package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentGenreRequests {

  private final String contentGenreBaseUrl = "/v1/content-genres";

  @Autowired private Request request;

  public Request getGetAllContentGenreRequest() {
    return request.clear().setGetStrategy().setUrlPattern(contentGenreBaseUrl);
  }

  public Request searchContentGenre() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            contentGenreBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
