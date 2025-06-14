package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentRatingRequests {

  private final String contentRatingBaseUrl = "/v1/content-ratings";

  @Autowired private Request request;

  public Request getGetAllContentRatingRequest() {
    return request.clear().setGetStrategy().setUrlPattern(contentRatingBaseUrl);
  }

  public Request searchContentRating() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            contentRatingBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
