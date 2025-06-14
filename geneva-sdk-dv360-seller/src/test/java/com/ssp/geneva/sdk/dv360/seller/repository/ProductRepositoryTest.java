package com.ssp.geneva.sdk.dv360.seller.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import com.ssp.geneva.sdk.dv360.seller.model.Product;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

  private String updateFields =
      "displayName,startTime,endTime,rateDetails.rateType,rateDetails.rate"
          + ",creativeConfig.creativeType,creativeConfig.dimensionCreativeConfig.width"
          + ",creativeConfig.dimensionCreativeConfig.height";

  private AccessToken token;

  private String dv360Endpoint = "localhost:8080";

  private String dv360ExchangeId = "10005";

  @InjectMocks private ProductRepository productRepository;

  @Mock RestTemplate dv360SellerRestTemplate;

  @Mock GoogleCredentials googleCredentials;

  @BeforeEach
  void setUp() {
    openMocks(this);
    String access_token = "b89fba14-24ac-481b-a1e2-664c81af8888";
    OAuth2Credentials credentials =
        OAuth2Credentials.newBuilder()
            .setAccessToken(new AccessToken(access_token, new Date()))
            .build();
    token = credentials.getAccessToken();
    productRepository =
        new ProductRepository(
            dv360Endpoint, dv360ExchangeId, dv360SellerRestTemplate, null, googleCredentials);
  }

  @Test
  void testCreated() {
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    Product createdProduct = Product.builder().build();
    createdProduct.setName("name");

    ResponseEntity<Product> createdResponse = ResponseEntity.of(Optional.of(createdProduct));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Product.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Product> response = productRepository.create(createdOrder, createdProduct);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdProduct.getName(), response.getBody().getName());
  }

  @Test
  void testRead() {
    String name = "name";
    Product createdProduct = Product.builder().build();
    createdProduct.setName("name");
    ResponseEntity<Product> createdResponse = ResponseEntity.of(Optional.of(createdProduct));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Product.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Product> response = productRepository.read(name);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdProduct.getName(), response.getBody().getName());
  }

  @Test
  void testUpdate() {
    String name = "name";
    Product createdProduct = Product.builder().build();
    createdProduct.setName("name");
    ResponseEntity<Product> createdResponse = ResponseEntity.of(Optional.of(createdProduct));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Product.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Product> response = productRepository.update(name, createdProduct, updateFields);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdProduct.getName(), response.getBody().getName());
  }
}
