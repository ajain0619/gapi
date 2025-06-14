package com.ssp.geneva.sdk.dv360.seller.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

  private AccessToken token;

  private String dv360Endpoint = "localhost:8080";

  private String dv360ExchangeId = "10005";

  private String updateFields = "displayName,partnerId,publisherName,publisherEmail";

  @InjectMocks private OrderRepository orderRepository;

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
    orderRepository =
        new OrderRepository(
            dv360Endpoint, dv360ExchangeId, dv360SellerRestTemplate, null, googleCredentials);
  }

  @Test
  void testCreated() {
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    ResponseEntity<Order> createdResponse = ResponseEntity.of(Optional.of(createdOrder));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Order.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Order> response = orderRepository.create(createdOrder);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdOrder.getName(), response.getBody().getName());
  }

  @Test
  void testRead() {
    String name = "name";
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    ResponseEntity<Order> createdResponse = ResponseEntity.of(Optional.of(createdOrder));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Order.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Order> response = orderRepository.read(name);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdOrder.getName(), response.getBody().getName());
  }

  @Test
  void shouldReturnOrderForGivenFilter() {
    Dv360PagedResponse<Order> dv360PagedResponse = Dv360PagedResponse.<Order>builder().build();
    var order = Order.builder().name("name").build();
    dv360PagedResponse.setResponse(List.of(order));
    ResponseEntity<Dv360PagedResponse<Order>> createdResponse =
        ResponseEntity.ok(dv360PagedResponse);
    when(dv360SellerRestTemplate.exchange(
            any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Dv360PagedResponse<Order>> response = orderRepository.get("nextPage", "filter");
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(order.getName(), response.getBody().getResponse().get(0).getName());
  }

  @Test
  void testUpdate() {
    String name = "name";
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    ResponseEntity<Order> createdResponse = ResponseEntity.of(Optional.of(createdOrder));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Order.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Order> response = orderRepository.update(name, createdOrder, updateFields);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdOrder.getName(), response.getBody().getName());
  }

  @Test
  void testCredsFail() throws IOException {
    String name = "name";
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    ResponseEntity<Order> createdResponse = ResponseEntity.of(Optional.of(createdOrder));
    doThrow(new IOException()).when(googleCredentials).refreshIfExpired();
    var exception = assertThrows(Dv360SellerSdkException.class, () -> orderRepository.read(name));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR,
        exception.getErrorCode());
  }

  @Test
  void testFailedRequest() {
    String name = "name";
    Order createdOrder = Order.builder().build();
    createdOrder.setName("name");
    ResponseEntity<Order> createdResponse = ResponseEntity.of(Optional.of(createdOrder));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(Order.class)))
        .thenThrow(RuntimeException.class);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    var exception = assertThrows(Dv360SellerSdkException.class, () -> orderRepository.read(name));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
  }
}
