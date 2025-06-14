package com.ssp.geneva.sdk.xandr.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import com.ssp.geneva.sdk.xandr.model.Deal;
import com.ssp.geneva.sdk.xandr.model.DealResponse;
import com.ssp.geneva.sdk.xandr.model.XandrResponse;
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
class DealRepositoryTest {

  private String xandrEndpoint = "localhost:8080";

  @InjectMocks private DealRepository dealRepository;

  @Mock RestTemplate xandrRestTemplate;
  @Mock AuthRepository authRepository;

  @BeforeEach
  void setUp() {
    dealRepository = new DealRepository(xandrEndpoint, xandrRestTemplate, authRepository);
  }

  @Test
  void shouldCreatedDealForXandr() {
    String dealName = "TestDeal";
    Deal deal = getTestDeal(dealName, 123);
    DealResponse dealResponse =
        DealResponse.builder()
            .response(XandrResponse.<Deal>builder().content(deal).build())
            .build();
    ResponseEntity<DealResponse> createdDealResponse = ResponseEntity.of(Optional.of(dealResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(DealResponse.class)))
        .thenReturn(createdDealResponse);

    ResponseEntity<Deal> response = dealRepository.createForXandr(deal);

    verify(authRepository, times(1)).getAuthHeaderForXandr();
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(dealName, response.getBody().getName());
  }

  @Test
  void shouldCreateDealForXandrMsRebroadcast() {
    String dealName = "TestDeal";
    Deal deal = getTestDeal(dealName, 123);
    DealResponse dealResponse =
        DealResponse.builder()
            .response(XandrResponse.<Deal>builder().content(deal).build())
            .build();
    ResponseEntity<DealResponse> createdDealResponse = ResponseEntity.of(Optional.of(dealResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(DealResponse.class)))
        .thenReturn(createdDealResponse);

    ResponseEntity<Deal> response = dealRepository.createForXandrMsRebroadcast(deal);

    verify(authRepository, times(1)).getAuthHeaderForXandrMsRebroadcast();
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(dealName, response.getBody().getName());
  }

  @Test
  void shouldUpdateDealForXandr() {
    String dealName = "TestDeal";
    Deal deal = getTestDeal(dealName, 123);
    DealResponse dealResponse =
        DealResponse.builder()
            .response(XandrResponse.<Deal>builder().content(deal).build())
            .build();
    ResponseEntity<DealResponse> updatedDealResponse = ResponseEntity.of(Optional.of(dealResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(DealResponse.class)))
        .thenReturn(updatedDealResponse);

    ResponseEntity<Deal> response = dealRepository.updateForXandr(deal);

    verify(authRepository, times(1)).getAuthHeaderForXandr();
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(dealName, response.getBody().getName());
  }

  @Test
  void shouldUpdateDealForXandrMsRebroadcast() {
    String dealName = "TestDeal";
    Deal deal = getTestDeal(dealName, 123);
    DealResponse dealResponse =
        DealResponse.builder()
            .response(XandrResponse.<Deal>builder().content(deal).build())
            .build();
    ResponseEntity<DealResponse> updatedDealResponse = ResponseEntity.of(Optional.of(dealResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(DealResponse.class)))
        .thenReturn(updatedDealResponse);

    ResponseEntity<Deal> response = dealRepository.updateForXandrMsRebroadcast(deal);

    verify(authRepository, times(1)).getAuthHeaderForXandrMsRebroadcast();
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(dealName, response.getBody().getName());
  }

  @Test
  void shouldThrowExceptionWithHttpClientError() {
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(Deal.class)))
        .thenThrow(RuntimeException.class);

    var exception =
        assertThrows(XandrSdkException.class, () -> dealRepository.createForXandr(null));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
  }

  private Deal getTestDeal(String name, int id) {
    return Deal.builder().name(name).id(id).build();
  }
}
