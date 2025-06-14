package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static java.util.Objects.isNull;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dv360SellerSdkDoubleClickBidManagerBeanFactory {
  private static DoubleClickBidManager doubleClickBidManager;

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link DoubleClickBidManager}
   */
  public static DoubleClickBidManager initDoubleClickBidManager(
      String dv360Endpoint, GoogleCredentials googleCredentials) {
    log.debug("null: {}", isNull(doubleClickBidManager));
    if (isNull(doubleClickBidManager)) {
      synchronized (DoubleClickBidManager.class) {
        doubleClickBidManager = newInstance(dv360Endpoint, googleCredentials);
      }
    }
    return doubleClickBidManager;
  }

  private static DoubleClickBidManager newInstance(
      String dv360Endpoint, GoogleCredentials googleCredentials) {
    HttpTransport httpTransport = null;

    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException | IOException e) {
      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR);
    }
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    HttpRequestInitializer httpRequestInitializer =
        request -> new HttpCredentialsAdapter(googleCredentials).initialize(request);
    return new DoubleClickBidManager.Builder(httpTransport, jsonFactory, httpRequestInitializer)
        .setRootUrl(dv360Endpoint)
        .build();
  }

  public static void reset() {
    doubleClickBidManager = null;
  }
}
