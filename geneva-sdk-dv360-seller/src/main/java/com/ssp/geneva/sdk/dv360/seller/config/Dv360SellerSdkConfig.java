package com.ssp.geneva.sdk.dv360.seller.config;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.Dv360SellerSdkClient;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkDoubleClickBidManagerBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkGoogleCredentialsBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkRestTemplateBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import java.text.SimpleDateFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
@Setter
@Configuration
public class Dv360SellerSdkConfig {

  @Value("${dv360.seller.endpoint}")
  private String dv360Endpoint;

  @Value("${dv360.seller.credentials}")
  private String dv360Credentials;

  @Value("${dv360.seller.exchange.id}")
  private String dv360ExchangeId;

  @Bean("dv360SdkConfigProperties")
  @ConditionalOnMissingBean
  public Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties() {
    log.debug("dv360Endpoint: {}", dv360Endpoint);
    log.debug("dv360ExchangeId: {}", dv360ExchangeId);
    return Dv360SellerSdkConfigProperties.builder()
        .dv360Endpoint(dv360Endpoint)
        .dv360ExchangeId(dv360ExchangeId)
        .dv360Credentials(dv360Credentials)
        .build();
  }

  @Bean("dv360SellerRestTemplate")
  public RestTemplate dv360SellerRestTemplate() {
    return Dv360SellerSdkRestTemplateBeanFactory.initRestTemplate();
  }

  @Bean("googleCredentials")
  public GoogleCredentials googleCredentials() {
    dv360Credentials = dv360Credentials.replaceAll("\n", "\\\\n");
    return Dv360SellerSdkGoogleCredentialsBeanFactory.initGoogleCredentials(dv360Credentials);
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
    log.debug(
        "auctionPackageRepository: dv360Endpoint: {}, dv360ExchangeId:{}",
        dv360Endpoint,
        dv360ExchangeId);
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
    log.debug(
        "orderRepository: dv360Endpoint: {}, dv360ExchangeId:{}", dv360Endpoint, dv360ExchangeId);
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
    log.debug(
        "productRepository: dv360Endpoint: {}, dv360ExchangeId:{}", dv360Endpoint, dv360ExchangeId);
    return ProductRepository.builder()
        .dv360Endpoint(dv360Endpoint)
        .dv360ExchangeId(dv360ExchangeId)
        .dv360SellerRestTemplate(dv360SellerRestTemplate)
        .doubleClickBidManager(doubleClickBidManager)
        .googleCredentials(googleCredentials)
        .build();
  }

  @Bean("dv360DateFormat")
  public SimpleDateFormat dv360DateFormat() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  }

  @Bean("dv360SellerSdkClient")
  public Dv360SellerSdkClient dv360SellerSdkClient(
      @Autowired Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties,
      @Autowired RestTemplate dv360SellerRestTemplate,
      @Autowired GoogleCredentials googleCredentials,
      @Autowired DoubleClickBidManager doubleClickBidManager,
      @Autowired AuctionPackageRepository auctionPackageRepository,
      @Autowired OrderRepository orderRepository,
      @Autowired ProductRepository productRepository,
      @Autowired SimpleDateFormat dv360DateFormat) {
    return Dv360SellerSdkClient.builder()
        .dv360SellerSdkConfigProperties(dv360SellerSdkConfigProperties)
        .doubleClickBidManager(doubleClickBidManager)
        .dv360SellerRestTemplate(dv360SellerRestTemplate)
        .googleCredentials(googleCredentials)
        .auctionPackageRepository(auctionPackageRepository)
        .orderRepository(orderRepository)
        .productRepository(productRepository)
        .dv360DateFormat(dv360DateFormat)
        .build();
  }
}
