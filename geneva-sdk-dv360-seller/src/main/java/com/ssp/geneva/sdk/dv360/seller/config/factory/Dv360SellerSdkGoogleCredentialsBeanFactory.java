package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static java.util.Objects.isNull;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManagerScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dv360SellerSdkGoogleCredentialsBeanFactory {
  private static GoogleCredentials googleCredentials;

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link GoogleCredentials}
   */
  public static GoogleCredentials initGoogleCredentials(String dv360Credentials) {
    if (isNull(googleCredentials)) {
      synchronized (GoogleCredentials.class) {
        googleCredentials = newInstance(dv360Credentials);
      }
    }
    return googleCredentials;
  }

  private static GoogleCredentials newInstance(String dv360Credentials) {
    try {
      return GoogleCredentials.fromStream(new ByteArrayInputStream(dv360Credentials.getBytes()))
          .createScoped(Collections.singleton(DoubleClickBidManagerScopes.DOUBLECLICKBIDMANAGER));
    } catch (IOException e) {
      log.error(e);
      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR);
    }
  }
}
