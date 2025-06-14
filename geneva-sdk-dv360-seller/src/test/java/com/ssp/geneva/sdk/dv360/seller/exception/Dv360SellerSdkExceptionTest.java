package com.ssp.geneva.sdk.dv360.seller.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import org.junit.jupiter.api.Test;

class Dv360SellerSdkExceptionTest {

  @Test
  void testCreateExceptionNoHttpStatus() {
    var ex =
        new Dv360SellerSdkException(
            Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR,
            new Object[] {"detail"});
    assertNotNull(ex);
    assertNotNull(ex.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR, ex.getErrorCode());

    assertNotNull(ex.getMessageParams());
    assertEquals("detail", ex.getMessageParams()[0]);
  }

  @Test
  void testCreateExceptionNoHttpStatusNoDetail() {
    var ex =
        new Dv360SellerSdkException(
            Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR);
    assertNotNull(ex);
    assertNotNull(ex.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR, ex.getErrorCode());

    assertNull(ex.getMessageParams());
  }
}
