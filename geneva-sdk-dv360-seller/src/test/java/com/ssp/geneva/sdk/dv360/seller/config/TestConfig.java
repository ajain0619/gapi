package com.ssp.geneva.sdk.dv360.seller.config;

import static org.mockito.MockitoAnnotations.openMocks;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkDoubleClickBidManagerBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkRestTemplateBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

  GoogleCredentials googleCredentials;

  private String dv360Endpoint = "localhost:8080";

  private String dv360ExchangeId = "10005";

  @BeforeEach
  public void setUp() {
    openMocks(this);
  }

  @Bean("dv360SdkConfigProperties")
  @ConditionalOnMissingBean
  public Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties() {
    return Dv360SellerSdkConfigProperties.builder()
        .dv360Endpoint("dv360Endpoint")
        .dv360ExchangeId("dv360ExchangeId")
        .dv360Credentials("dv360Credentials")
        .build();
  }

  @Bean("dv360SellerRestTemplate")
  public RestTemplate dv360SellerRestTemplate() {
    return Dv360SellerSdkRestTemplateBeanFactory.initRestTemplate();
  }

  @Bean("googleCredentials")
  public GoogleCredentials googleCredentials() {
    return GoogleCredentials.newBuilder().build();
  }

  @Bean("doubleClickBidManager")
  public DoubleClickBidManager doubleClickBidManager(
      @Autowired GoogleCredentials googleCredentials) {
    return Dv360SellerSdkDoubleClickBidManagerBeanFactory.initDoubleClickBidManager(
        dv360Endpoint, googleCredentials);
  }

  @Bean("auctionPackageRepository")
  public AuctionPackageRepository auctionPackageRepository(
      @Autowired RestTemplate dv360SellerRestTemplate,
      @Autowired GoogleCredentials googleCredentials,
      @Autowired DoubleClickBidManager doubleClickBidManager) {
    return AuctionPackageRepository.builder()
        .dv360Endpoint(dv360Endpoint)
        .dv360ExchangeId(dv360ExchangeId)
        .dv360SellerRestTemplate(dv360SellerRestTemplate)
        .doubleClickBidManager(doubleClickBidManager)
        .googleCredentials(googleCredentials)
        .build();
  }

  @Bean("orderRepository")
  public OrderRepository orderRepository(
      @Autowired RestTemplate dv360SellerRestTemplate,
      @Autowired GoogleCredentials googleCredentials,
      @Autowired DoubleClickBidManager doubleClickBidManager) {
    return OrderRepository.builder()
        .dv360Endpoint(dv360Endpoint)
        .dv360ExchangeId(dv360ExchangeId)
        .dv360SellerRestTemplate(dv360SellerRestTemplate)
        .doubleClickBidManager(doubleClickBidManager)
        .googleCredentials(googleCredentials)
        .build();
  }

  @Bean("productRepository")
  public ProductRepository productRepository(
      @Autowired RestTemplate dv360SellerRestTemplate,
      @Autowired GoogleCredentials googleCredentials,
      @Autowired DoubleClickBidManager doubleClickBidManager) {
    return ProductRepository.builder()
        .dv360Endpoint(dv360Endpoint)
        .dv360ExchangeId(dv360ExchangeId)
        .dv360SellerRestTemplate(dv360SellerRestTemplate)
        .doubleClickBidManager(doubleClickBidManager)
        .googleCredentials(googleCredentials)
        .build();
  }
}
