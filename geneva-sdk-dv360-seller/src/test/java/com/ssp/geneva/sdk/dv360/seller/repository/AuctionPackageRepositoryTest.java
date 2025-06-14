package com.ssp.geneva.sdk.dv360.seller.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.dv360.seller.model.AuctionPackage;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuctionPackageRepositoryTest {

  private AccessToken token;

  private String dv360Endpoint = "localhost:8080";

  private String dv360ExchangeId = "10005";

  private String updateFields =
      "displayName,description,floorPrice.currencyCode,floorPrice.units,floorPrice.nanos,startTime"
          + ",endTime,mediumType";

  @InjectMocks private AuctionPackageRepository auctionPackageRepository;

  @Mock RestTemplate dv360SellerRestTemplate;

  @Mock GoogleCredentials googleCredentials;

  @Mock DoubleClickBidManager doubleClickBidManager;

  @Mock Logger log;

  @BeforeEach
  void setUp() {
    openMocks(this);
    String access_token = "b89fba14-24ac-481b-a1e2-664c81af8888";
    OAuth2Credentials credentials =
        OAuth2Credentials.newBuilder()
            .setAccessToken(new AccessToken(access_token, new Date()))
            .build();
    token = credentials.getAccessToken();
    auctionPackageRepository =
        new AuctionPackageRepository(
            dv360Endpoint,
            dv360ExchangeId,
            dv360SellerRestTemplate,
            doubleClickBidManager,
            googleCredentials);
  }

  @Test
  void testCreated() {
    AuctionPackage createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("action");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(AuctionPackage.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<AuctionPackage> response =
        auctionPackageRepository.create(createdAuctionPackage);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdAuctionPackage.getName(), response.getBody().getName());
  }

  @Test
  void shouldReturnAuctionPackageForGivenName() {
    String name = "test";
    var createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(AuctionPackage.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<AuctionPackage> response = auctionPackageRepository.read(name);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdAuctionPackage.getName(), response.getBody().getName());
  }

  @Test
  void shouldReturnAuctionPackageForGivenFilter() {
    var dv360PagedResponse = Dv360PagedResponse.<AuctionPackage>builder().build();
    var createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    dv360PagedResponse.setResponse(List.of(createdAuctionPackage));
    ResponseEntity<Dv360PagedResponse> createdResponse =
        ResponseEntity.of(Optional.of(dv360PagedResponse));
    when(dv360SellerRestTemplate.exchange(
            any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .thenReturn(createdResponse);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    ResponseEntity<Dv360PagedResponse<AuctionPackage>> response =
        auctionPackageRepository.get("nextPage", "filter");
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(
        createdAuctionPackage.getName(), response.getBody().getResponse().get(0).getName());
  }

  @Test
  void shouldReturnUpdatedAuctionPackageWhenUpdate() {
    String name = "test";
    var createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(googleCredentials.getAccessToken()).thenReturn(token);
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(AuctionPackage.class)))
        .thenReturn(createdResponse);
    ResponseEntity<AuctionPackage> response =
        auctionPackageRepository.update(name, createdAuctionPackage, updateFields);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(createdAuctionPackage.getName(), response.getBody().getName());
  }

  @Test
  void testUpdate_httpError() {
    String name = "test";
    AuctionPackage createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(googleCredentials.getAccessToken()).thenReturn(token);
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(AuctionPackage.class)))
        .thenThrow(Dv360HttpStatusCodeException.class);
    var ex =
        assertThrows(
            Dv360SellerSdkException.class,
            () -> auctionPackageRepository.update(name, createdAuctionPackage, updateFields));
    assertEquals(Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, ex.getErrorCode());
  }

  @Test
  void shouldThrowDv360SellerSdkException() {
    String name = "test";
    AuctionPackage createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(googleCredentials.getAccessToken()).thenReturn(token);
    when(dv360SellerRestTemplate.exchange(any(RequestEntity.class), eq(AuctionPackage.class)))
        .thenThrow(RuntimeException.class);
    var ex =
        assertThrows(
            Dv360SellerSdkException.class,
            () -> auctionPackageRepository.update(name, createdAuctionPackage, updateFields));
    assertEquals(Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, ex.getErrorCode());
  }

  @Test
  void shouldThrowDv360SellerSdkExceptionOnGet() {
    String name = "test";
    AuctionPackage createdAuctionPackage = AuctionPackage.builder().build();
    createdAuctionPackage.setName("name");
    createdAuctionPackage.setDescription("description");
    ResponseEntity<AuctionPackage> createdResponse =
        ResponseEntity.of(Optional.of(createdAuctionPackage));
    when(googleCredentials.getAccessToken()).thenReturn(token);
    when(dv360SellerRestTemplate.exchange(
            any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .thenThrow(RuntimeException.class);
    var ex =
        assertThrows(
            Dv360SellerSdkException.class,
            () -> auctionPackageRepository.get("nextPage", "filter"));
    assertEquals(Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, ex.getErrorCode());
  }

  private class Dv360HttpStatusCodeException extends HttpStatusCodeException {
    public Dv360HttpStatusCodeException() {
      super(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void shouldLogRequestResponseException() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req =
        auctionPackageRepository.generateRequest("name", HttpMethod.POST, auctionPackage);
    ResponseEntity<AuctionPackage> resp = ResponseEntity.of(Optional.of(auctionPackage));
    Exception ex = new RuntimeException("test exception");
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: request: { url: 'localhost:8080/name', method: 'POST', body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } response: { status: 200, body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } exception: { name: 'java.lang.RuntimeException', message: 'test exception' } ",
        msg);
  }

  @Test
  void shouldLogRequestException() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req =
        auctionPackageRepository.generateRequest("name", HttpMethod.POST, auctionPackage);
    ResponseEntity<AuctionPackage> resp = null;
    Exception ex = new RuntimeException("test exception");
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: request: { url: 'localhost:8080/name', method: 'POST', body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } exception: { name: 'java.lang.RuntimeException', message: 'test exception' } ",
        msg);
  }

  @Test
  void shouldLogRequestResponse() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req =
        auctionPackageRepository.generateRequest("name", HttpMethod.POST, auctionPackage);
    ResponseEntity<AuctionPackage> resp = ResponseEntity.of(Optional.of(auctionPackage));
    Exception ex = null;
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: request: { url: 'localhost:8080/name', method: 'POST', body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } response: { status: 200, body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } ",
        msg);
  }

  @Test
  void shouldLogResponseException() {
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req = null;
    ResponseEntity<AuctionPackage> resp = ResponseEntity.of(Optional.of(auctionPackage));
    Exception ex = new RuntimeException("test exception");
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: response: { status: 200, body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } exception: { name: 'java.lang.RuntimeException', message: 'test exception' } ",
        msg);
  }

  @Test
  void shouldLogRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req =
        auctionPackageRepository.generateRequest("name", HttpMethod.POST, auctionPackage);
    ResponseEntity<AuctionPackage> resp = null;
    Exception ex = null;
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: request: { url: 'localhost:8080/name', method: 'POST', body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } ",
        msg);
  }

  @Test
  void shouldLogResponse() {
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req = null;
    ResponseEntity<AuctionPackage> resp = ResponseEntity.of(Optional.of(auctionPackage));
    Exception ex = null;
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: response: { status: 200, body: 'AuctionPackage(name=null, displayName=test deal, status=null, description=null, format=null, externalDealId=null, logoUrl=null, floorPrice=null, startTime=null, endTime=null, mediumType=null)' } ",
        msg);
  }

  @Test
  void shouldLogException() {
    RequestEntity<AuctionPackage> req = null;
    ResponseEntity<AuctionPackage> resp = null;
    Exception ex = new RuntimeException("test exception");
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals(
        "Request exception: exception: { name: 'java.lang.RuntimeException', message: 'test exception' } ",
        msg);
  }

  @Test
  void shouldLogNothing() {
    AuctionPackage auctionPackage = AuctionPackage.builder().displayName("test deal").build();
    RequestEntity<AuctionPackage> req = null;
    ResponseEntity<AuctionPackage> resp = null;
    Exception ex = null;
    String msg = auctionPackageRepository.buildRequestFailureLogMessage(req, resp, ex);
    assertEquals("", msg);
  }
}
