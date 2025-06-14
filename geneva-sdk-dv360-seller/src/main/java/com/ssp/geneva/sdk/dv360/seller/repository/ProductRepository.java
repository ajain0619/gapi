package com.ssp.geneva.sdk.dv360.seller.repository;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import com.ssp.geneva.sdk.dv360.seller.model.Product;
import com.ssp.geneva.sdk.dv360.seller.model.request.UpdateProductRequest;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class ProductRepository extends BaseRestRepository {
  private static final String ORDER_PRODUCT_NAME = "%s/products";

  @Builder
  public ProductRepository(
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
   * POST to DV360 service to create an {@link Product}
   *
   * @param order {@link Order} in which to create the new {@link Product}
   * @param product {@link Product} to create
   * @return {@link ResponseEntity<Product>} containing the new {@link Product}
   */
  public ResponseEntity<Product> create(Order order, Product product) {
    log.debug("ProductRepository.create()");
    var name = String.format(ORDER_PRODUCT_NAME, order.getName());
    RequestEntity<Product> request = generateRequest(name, HttpMethod.POST, product);
    return sendRequest(request, Product.class);
  }

  /**
   * GET to DV360 service to read an {@link Product}
   *
   * @param name {@link String} to read
   * @return {@link ResponseEntity<Product>} containing the requested {@link Product}
   */
  public ResponseEntity<Product> read(String name) {
    log.debug("ProductRepository.read()");
    RequestEntity<Product> request =
        generateRequest(name, HttpMethod.GET, Product.builder().build());
    return sendRequest(request, Product.class);
  }

  /**
   * PATCH to DV360 service to update an {@link Product}
   *
   * @param name {@link String} to read
   * @param product {@link Product} to update
   * @param updateMask {@link String} comma separated list of fields to update
   * @return {@link ResponseEntity<Product>} containing the updated {@link Product}
   */
  public ResponseEntity<Product> update(String name, Product product, String updateMask) {
    log.debug("ProductRepository.update()");
    UpdateProductRequest updateProductReq =
        UpdateProductRequest.builder().product(product).updateMask(updateMask).build();
    RequestEntity<UpdateProductRequest> request =
        generateRequest(name, HttpMethod.PATCH, updateProductReq);
    return sendRequest(request, UpdateProductRequest.class, Product.class);
  }
}
