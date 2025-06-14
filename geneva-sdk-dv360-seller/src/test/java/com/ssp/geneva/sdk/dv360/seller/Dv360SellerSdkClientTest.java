package com.ssp.geneva.sdk.dv360.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.Dv360SellerSdkConfigProperties;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

class Dv360SellerSdkClientTest {

  @InjectMocks Dv360SellerSdkClient dv360SellerSdkClient;

  @Mock Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties;
  @Mock RestTemplate dv360SellerRestTemplate;
  @Mock GoogleCredentials googleCredentials;
  @Mock DoubleClickBidManager doubleClickBidManager;
  @Mock AuctionPackageRepository auctionPackageRepository;
  @Mock OrderRepository orderRepository;
  @Mock ProductRepository productRepository;
  @Mock SimpleDateFormat dv360DateFormat;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void testConstructor() {
    dv360SellerSdkClient =
        Dv360SellerSdkClient.builder()
            .dv360SellerSdkConfigProperties(dv360SellerSdkConfigProperties)
            .dv360SellerRestTemplate(dv360SellerRestTemplate)
            .auctionPackageRepository(auctionPackageRepository)
            .doubleClickBidManager(doubleClickBidManager)
            .googleCredentials(googleCredentials)
            .productRepository(productRepository)
            .orderRepository(orderRepository)
            .dv360DateFormat(dv360DateFormat)
            .build();
    assertEquals(
        dv360SellerSdkConfigProperties, dv360SellerSdkClient.getDv360SellerSdkConfigProperties());
    assertEquals(dv360SellerRestTemplate, dv360SellerSdkClient.getDv360SellerRestTemplate());
    assertEquals(auctionPackageRepository, dv360SellerSdkClient.getAuctionPackageRepository());
    assertEquals(doubleClickBidManager, dv360SellerSdkClient.getDoubleClickBidManager());
    assertEquals(googleCredentials, dv360SellerSdkClient.getGoogleCredentials());
    assertEquals(productRepository, dv360SellerSdkClient.getProductRepository());
    assertEquals(orderRepository, dv360SellerSdkClient.getOrderRepository());
    assertEquals(dv360DateFormat, dv360SellerSdkClient.getDv360DateFormat());
  }
}
