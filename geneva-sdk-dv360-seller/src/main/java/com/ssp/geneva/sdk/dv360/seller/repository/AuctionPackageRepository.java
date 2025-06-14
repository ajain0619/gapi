package com.ssp.geneva.sdk.dv360.seller.repository;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.model.AuctionPackage;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import com.ssp.geneva.sdk.dv360.seller.model.request.UpdateAuctionPackageRequest;
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
public class AuctionPackageRepository extends BaseRestRepository {
  private static final String AUCTION_PACKAGE_NAME = "exchanges/%s/auctionPackages";
  private static final String QUERY_PARAM = "?pageToken=%s&filter=externalDealId=%s";

  @Builder
  public AuctionPackageRepository(
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
   * POST to DV360 service to create an {@link AuctionPackage}
   *
   * @param auctionPackage {@link AuctionPackage} to create
   * @return {@link ResponseEntity<AuctionPackage>} containing the new {@link AuctionPackage}
   */
  public ResponseEntity<AuctionPackage> create(AuctionPackage auctionPackage) {
    log.debug("AuctionPackageRepository.create()");
    String name = String.format(AUCTION_PACKAGE_NAME, dv360ExchangeId);
    RequestEntity<AuctionPackage> request = generateRequest(name, HttpMethod.POST, auctionPackage);
    return sendRequest(request, AuctionPackage.class);
  }

  /**
   * GET to DV360 service to read an {@link AuctionPackage}
   *
   * @param auctionPackageId {@link String} to read
   * @return {@link ResponseEntity<AuctionPackage>} containing the requested {@link AuctionPackage}
   */
  public ResponseEntity<AuctionPackage> read(String auctionPackageId) {
    log.debug("AuctionPackageRepository.read()");
    var name = String.format(AUCTION_PACKAGE_NAME, dv360ExchangeId).concat("/" + auctionPackageId);
    RequestEntity<AuctionPackage> request =
        generateRequest(name, HttpMethod.GET, AuctionPackage.builder().build());
    return sendRequest(request, AuctionPackage.class);
  }

  /**
   * GET to DV360 service to get an {@link Dv360PagedResponse}
   *
   * @param token {@link String} to token
   * @param filter {@link String} to filter
   * @return {@link ResponseEntity<Dv360PagedResponse<AuctionPackage>>} containing the requested
   *     {@link Dv360PagedResponse}
   */
  public ResponseEntity<Dv360PagedResponse<AuctionPackage>> get(String token, String filter) {
    log.debug("AuctionPackageRepository.get()");
    var queryParam =
        String.format(QUERY_PARAM, URLEncoder.encode(token, StandardCharsets.UTF_8), filter);
    String name = String.format(AUCTION_PACKAGE_NAME, dv360ExchangeId).concat(queryParam);

    RequestEntity<Dv360PagedResponse<AuctionPackage>> request =
        generateRequest(name, HttpMethod.GET, Dv360PagedResponse.<AuctionPackage>builder().build());

    var resolvableType =
        ResolvableType.forClassWithGenerics(Dv360PagedResponse.class, AuctionPackage.class);
    ParameterizedTypeReference<Dv360PagedResponse<AuctionPackage>> typeRef =
        ParameterizedTypeReference.forType(resolvableType.getType());

    return sendRequestWithTypeReference(request, typeRef);
  }

  /**
   * PATCH to DV360 service to update an {@link AuctionPackage}
   *
   * @param name {@link String} to read
   * @param auctionPackage {@link AuctionPackage} to update
   * @return {@link ResponseEntity<AuctionPackage>} containing the updated {@link AuctionPackage}
   */
  public ResponseEntity<AuctionPackage> update(
      String name, AuctionPackage auctionPackage, String updateMask) {
    log.debug("AuctionPackageRepository.update()");
    UpdateAuctionPackageRequest updateAuctionPackageReq =
        UpdateAuctionPackageRequest.builder()
            .auctionPackage(auctionPackage)
            .updateMask(updateMask)
            .build();
    RequestEntity<UpdateAuctionPackageRequest> request =
        generateRequest(name, HttpMethod.PATCH, updateAuctionPackageReq);
    return sendRequest(request, UpdateAuctionPackageRequest.class, AuctionPackage.class);
  }
}
