package com.ssp.geneva.sdk.identityb2b.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuildUri {

  /**
   * Build URL based on params.
   *
   * @param baseUrl main url
   * @param path resource path
   * @return well formed url
   */
  public static String build(String baseUrl, String path) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path(path);
    String url = builder.build().toUriString();
    validateUrl(url);
    return url;
  }

  private static void validateUrl(String url) {
    if (!new UrlValidator().isValid(url)) {
      log.error("Error while parsing URL: {} ", url);
      throw new RuntimeException();
    }
  }
}
