package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagsRequest {

  @Autowired private Request request;

  public Request getTags() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sellers/"
                    + RequestParams.PUBLISHER_PID
                    + "/sites/"
                    + RequestParams.SITE_PID
                    + "/placements/"
                    + RequestParams.POSITION_PID
                    + "/tags?size="
                    + RequestParams.SIZE
                    + "&page="
                    + RequestParams.PAGE));
  }
}
