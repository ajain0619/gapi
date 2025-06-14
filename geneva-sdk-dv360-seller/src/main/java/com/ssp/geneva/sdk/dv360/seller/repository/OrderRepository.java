package com.ssp.geneva.sdk.dv360.seller.repository;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import com.ssp.geneva.sdk.dv360.seller.model.request.UpdateOrderRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class OrderRepository extends BaseRestRepository {
  private static final String ORDER_NAME = "exchanges/%s/orders";
  private static final String QUERY_PARAM = "?pageToken=%s&filter=%s";

  @Builder
  public OrderRepository(
      String dv360Endpoint,
      String dv360ExchangeId,
      RestTemplate dv360SellerRestTemplate,
      DoubleClickBidManager doubleClickBidManager,
      GoogleCredentials googleCredentials) {
    super(
        dv360Endpoint,
        dv360ExchangeId,
        dv360SellerRestTemplate,
        doubleClickBidManager,
        googleCredentials);
  }

  /**
   * POST to DV360 service to create an {@link Order}
   *
   * @param order {@link Order} to create
   * @return {@link ResponseEntity<Order>} containing the new {@link Order}
   */
  public ResponseEntity<Order> create(Order order) {
    log.debug("OrderRepository.create()");
    String name = String.format(ORDER_NAME, dv360ExchangeId);
    RequestEntity<Order> request = generateRequest(name, HttpMethod.POST, order);
    return sendRequest(request, Order.class);
  }

  /**
   * GET to DV360 service to read an {@link Order}
   *
   * @param name {@link String} to read
   * @return {@link ResponseEntity<Order>} containing the requested {@link Order}
   */
  public ResponseEntity<Order> read(String name) {
    log.debug("OrderRepository.read()");
    RequestEntity<Order> request = generateRequest(name, HttpMethod.GET, Order.builder().build());
    return sendRequest(request, Order.class);
  }

  /**
   * GET to DV360 service to read an {@link Dv360PagedResponse}
   *
   * @param token {@link String} to next page token
   * @param filter {@link String} to filter
   * @return {@link ResponseEntity<Dv360PagedResponse>} containing the requested {@link Order}
   */
  public ResponseEntity<Dv360PagedResponse<Order>> get(String token, String filter) {
    log.debug("OrderRepository.get()");
    var queryParam =
        String.format(QUERY_PARAM, URLEncoder.encode(token, StandardCharsets.UTF_8), filter);
    String name = String.format(ORDER_NAME, dv360ExchangeId).concat(queryParam);
    RequestEntity<Dv360PagedResponse<Order>> request =
        generateRequest(name, HttpMethod.GET, Dv360PagedResponse.<Order>builder().build());
    var resolvableType = ResolvableType.forClassWithGenerics(Dv360PagedResponse.class, Order.class);
    ParameterizedTypeReference<Dv360PagedResponse<Order>> typeRef =
        ParameterizedTypeReference.forType(resolvableType.getType());
    return sendRequestWithTypeReference(request, typeRef);
  }

  /**
   * PATCH to DV360 service to update an {@link Order}
   *
   * @param name {@link String} to read
   * @param order {@link Order} to update
   * @return {@link ResponseEntity<Order>} containing the updated {@link Order}
   */
  public ResponseEntity<Order> update(String name, Order order, String updateMask) {
    log.debug("OrderRepository.update()");
    UpdateOrderRequest updateOrderReq =
        UpdateOrderRequest.builder().order(order).updateMask(updateMask).build();
    RequestEntity<UpdateOrderRequest> request =
        generateRequest(name, HttpMethod.PATCH, updateOrderReq);
    return sendRequest(request, UpdateOrderRequest.class, Order.class);
  }
}
