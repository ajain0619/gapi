package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Dv360SellerSdkDoubleClickBidManagerBeanFactoryTest {

  private String endpoint = "localhost:8080";

  @Mock private GoogleCredentials googleCredentials;
  private static MockedStatic<GoogleNetHttpTransport> mockedStatic;

  @BeforeEach
  void setUp() {
    openMocks(this);
    mockedStatic = mockStatic(GoogleNetHttpTransport.class);
  }

  @AfterEach
  void after() {
    mockedStatic.close();
  }

  @Test
  void testBuildObjectSuccessfully() {
    mockedStatic.when(GoogleNetHttpTransport::newTrustedTransport).thenCallRealMethod();
    Dv360SellerSdkDoubleClickBidManagerBeanFactory.reset();
    final DoubleClickBidManager doubleClickBidManager =
        Dv360SellerSdkDoubleClickBidManagerBeanFactory.initDoubleClickBidManager(
            endpoint, googleCredentials);
    assertNotNull(doubleClickBidManager);

    final DoubleClickBidManager doubleClickBidManager2 =
        Dv360SellerSdkDoubleClickBidManagerBeanFactory.initDoubleClickBidManager(
            endpoint, googleCredentials);
    assertNotNull(doubleClickBidManager2);
  }

  @Test
  void testFailedInitMethod() {
    mockedStatic
        .when(GoogleNetHttpTransport::newTrustedTransport)
        .thenThrow(GeneralSecurityException.class);
    Dv360SellerSdkDoubleClickBidManagerBeanFactory.reset();
    var exception =
        assertThrows(
            Dv360SellerSdkException.class,
            () ->
                Dv360SellerSdkDoubleClickBidManagerBeanFactory.initDoubleClickBidManager(
                    endpoint, googleCredentials));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR,
        exception.getErrorCode());
  }

  @Test
  void testFailedInitMethod2() {
    mockedStatic.when(GoogleNetHttpTransport::newTrustedTransport).thenThrow(IOException.class);
    Dv360SellerSdkDoubleClickBidManagerBeanFactory.reset();
    var exception =
        assertThrows(
            Dv360SellerSdkException.class,
            () ->
                Dv360SellerSdkDoubleClickBidManagerBeanFactory.initDoubleClickBidManager(
                    endpoint, googleCredentials));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR,
        exception.getErrorCode());
  }
}
