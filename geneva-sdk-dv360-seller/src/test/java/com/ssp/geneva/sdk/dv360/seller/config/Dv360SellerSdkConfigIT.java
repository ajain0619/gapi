package com.ssp.geneva.sdk.dv360.seller.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource("classpath:application-test.properties")
class Dv360SellerSdkConfigIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void testApplicationPropertiesSet() {
    final Environment environment = applicationContext.getEnvironment();
    final String endpoint = environment.getProperty("dv360.seller.endpoint");
    final String exchange_id = environment.getProperty("dv360.seller.exchange.id");

    assertEquals("http://127.0.0.1", endpoint);
    assertEquals(10005, Integer.valueOf(exchange_id).intValue());
  }

  @Test
  void shouldRegisterExpectedBeans() {
    Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties =
        (Dv360SellerSdkConfigProperties) applicationContext.getBean("dv360SdkConfigProperties");
    assertNotNull(dv360SellerSdkConfigProperties);

    GoogleCredentials googleCredentials =
        (GoogleCredentials) applicationContext.getBean("googleCredentials");
    assertNotNull(googleCredentials);

    RestTemplate dv360SellerRestTemplate =
        (RestTemplate) applicationContext.getBean("dv360SellerRestTemplate");
    assertNotNull(dv360SellerRestTemplate);

    DoubleClickBidManager doubleClickBidManager =
        (DoubleClickBidManager) applicationContext.getBean("doubleClickBidManager");
    assertNotNull(doubleClickBidManager);

    AuctionPackageRepository auctionPackageRepository =
        (AuctionPackageRepository) applicationContext.getBean("auctionPackageRepository");
    assertNotNull(auctionPackageRepository);

    OrderRepository orderRepository =
        (OrderRepository) applicationContext.getBean("orderRepository");
    assertNotNull(orderRepository);

    ProductRepository productRepository =
        (ProductRepository) applicationContext.getBean("productRepository");
    assertNotNull(productRepository);
  }
}
