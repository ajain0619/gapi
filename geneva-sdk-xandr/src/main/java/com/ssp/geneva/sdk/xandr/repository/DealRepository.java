package com.ssp.geneva.sdk.xandr.repository;

import com.ssp.geneva.sdk.xandr.model.Deal;
import com.ssp.geneva.sdk.xandr.model.DealRequest;
import com.ssp.geneva.sdk.xandr.model.DealResponse;
import com.ssp.geneva.sdk.xandr.model.XandrResponse;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * This repository class handles requests made to the Xandr Deal Service:
 * https://docs.xandr.com/bundle/xandr-api/page/deal-service.html#DealService-Seller
 */
@Log4j2
public class DealRepository extends BaseRestRepository {

  private final AuthRepository authRepository;

  private static final String DEAL_REST_PATH = "deal";

  @Builder
  public DealRepository(
      String xandrEndpoint, RestTemplate xandrRestTemplate, AuthRepository authRepository) {
    super(xandrEndpoint, xandrRestTemplate);
    this.authRepository = authRepository;
  }

  /**
   * POST to Xandr deal service to create a {@link Deal} for Xandr user account
   *
   * @param deal {@link Deal} to create
   * @return {@link ResponseEntity <Deal>} containing the new {@link Deal}
   */
  public ResponseEntity<Deal> createForXandr(Deal deal) {
    HttpHeaders authHeader = authRepository.getAuthHeaderForXandr();
    return create(deal, authHeader);
  }

  /**
   * POST to Xandr deal service to create a {@link Deal} for Xandr Microsoft Rebroadcasting user
   * account
   *
   * @param deal {@link Deal} to create
   * @return {@link ResponseEntity <Deal>} containing the new {@link Deal}
   */
  public ResponseEntity<Deal> createForXandrMsRebroadcast(Deal deal) {
    HttpHeaders authHeader = authRepository.getAuthHeaderForXandrMsRebroadcast();
    return create(deal, authHeader);
  }

  private ResponseEntity<Deal> create(Deal deal, HttpHeaders authHeader) {
    log.debug("DealRepository.create()");
    DealRequest xandrDealRequest = DealRequest.builder().deal(deal).build();
    RequestEntity<DealRequest> request =
        generateRequest(DEAL_REST_PATH, HttpMethod.POST, xandrDealRequest, authHeader);
    ResponseEntity<DealResponse> response =
        sendRequest(request, DealRequest.class, DealResponse.class);
    return new ResponseEntity<>(getDeal(response), response.getStatusCode());
  }

  /**
   * PUT to Xandr deal service to update a {@link Deal} for Xandr user account
   *
   * @param deal {@link Deal} to create
   * @return {@link ResponseEntity <Deal>} containing the new {@link Deal}
   */
  public ResponseEntity<Deal> updateForXandr(Deal deal) {
    HttpHeaders authHeader = authRepository.getAuthHeaderForXandr();
    return update(deal, authHeader);
  }

  /**
   * PUT to Xandr deal service to update a {@link Deal} for Xandr Microsoft Rebroadcasting user
   * account
   *
   * @param deal {@link Deal} to create
   * @return {@link ResponseEntity <Deal>} containing the new {@link Deal}
   */
  public ResponseEntity<Deal> updateForXandrMsRebroadcast(Deal deal) {
    HttpHeaders authHeader = authRepository.getAuthHeaderForXandrMsRebroadcast();
    return update(deal, authHeader);
  }

  private ResponseEntity<Deal> update(Deal deal, HttpHeaders authHeader) {
    log.debug("DealRepository.update()");
    String restPath = String.format("%s?id=%d", DEAL_REST_PATH, deal.getId());
    DealRequest xandrDealRequest = DealRequest.builder().deal(deal).build();
    RequestEntity<DealRequest> request =
        generateRequest(restPath, HttpMethod.PUT, xandrDealRequest, authHeader);
    ResponseEntity<DealResponse> response =
        sendRequest(request, DealRequest.class, DealResponse.class);
    return new ResponseEntity<>(getDeal(response), response.getStatusCode());
  }

  private Deal getDeal(ResponseEntity<DealResponse> dealResponse) {
    return Optional.ofNullable(dealResponse)
        .map(ResponseEntity::getBody)
        .map(DealResponse::getResponse)
        .map(XandrResponse::getContent)
        .orElse(null);
  }
}
