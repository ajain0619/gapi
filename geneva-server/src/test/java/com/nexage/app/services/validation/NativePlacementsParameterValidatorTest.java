package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.CrudOperation;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.ResourceLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NativePlacementsParameterValidatorTest {
  private static final long VALID_SELLER_ID = 248L;
  private static final long VALID_SITE_ID = 456L;
  private static final long VALID_POSITION_ID = 100371L;
  private static final String VALID_REQ_NATIVE =
      "/data/nativeplacement/create/native_placement_created_dto.json";
  private static ObjectMapper objectMapper;

  private NativePlacementsParameterValidator validator = new NativePlacementsParameterValidator();
  private NativePlacementDTO nativePlacementDTO;

  @BeforeAll
  public static void setUpBeforeAll() {
    objectMapper = new CustomObjectMapper();
  }

  @BeforeEach
  @SneakyThrows
  public void setUp() {
    nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(VALID_REQ_NATIVE), NativePlacementDTO.class);
  }

  @Test
  void createPlacement() {
    validator.validateByOperation(
        CrudOperation.CREATE,
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .nativePlacement(nativePlacementDTO)
            .build());
  }

  @Test
  void updatePlacement() {
    validator.validateByOperation(
        CrudOperation.UPDATE,
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .placementId(VALID_POSITION_ID)
            .nativePlacement(nativePlacementDTO)
            .build());
  }

  @Test
  void createPlacement_sellerIdIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .siteId(VALID_SITE_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.CREATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void createPlacement_siteIdIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.CREATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void createPlacement_nativePlacementDTOIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.CREATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_sellerIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .siteId(VALID_SITE_ID)
            .placementId(VALID_POSITION_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_siteIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .placementId(VALID_POSITION_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_positionIsNull() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_updateEntity() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .placementId(VALID_POSITION_ID)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_positionIsDifferentInUrl() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(VALID_SITE_ID)
            .placementId(1L)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void updateNativePlacement_siteIdOnPayloadIsDifferentThanUrl() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(1L)
            .placementId(VALID_POSITION_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalStateException.class,
        () -> validator.validateByOperation(CrudOperation.UPDATE, nativePlacementRequestParamsDTO));
  }

  @Test
  void createNativePlacement_siteIdOnPayloadIsDifferentThanUrl() {
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(VALID_SELLER_ID)
            .siteId(1L)
            .placementId(VALID_POSITION_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        IllegalStateException.class,
        () -> validator.validateByOperation(CrudOperation.CREATE, nativePlacementRequestParamsDTO));
  }
}
