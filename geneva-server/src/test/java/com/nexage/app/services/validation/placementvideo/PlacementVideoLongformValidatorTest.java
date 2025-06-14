package com.nexage.app.services.validation.placementvideo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementVideoLongformConstraint;
import com.nexage.app.util.validator.placement.PlacementVideoLongformValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementVideoLongformValidatorTest extends BaseValidatorTest {

  @Mock private PlacementVideoLongformConstraint annotation;

  @InjectMocks
  private PlacementVideoLongformValidator validator = new PlacementVideoLongformValidator();

  @BeforeEach
  public void setup() throws Exception {
    initializeContext();
    initializeConstraint();
  }

  private static final String testPlayerBrand = "test_player";

  static Object[][] data() {
    return new Object[][] {
      {null, true},
      {"testplayer", true},
      {"Test_Player123", true},
      {"TestPlayer#", false},
      {"[Test_Player", false},
      {"TestPlayer&", false},
      {"PlayerBrand12~", true},
      {"Player1Brand2.-", true},
      {"_~Player1Brand2.-", true},
      {"_+Player1Brand2.-", false},
      {"Player1Brand$#%^!@", false},
    };
  }

  @Test
  void testLongformValidations() {

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    // longform false
    assertTrue(validator.isValid(placementVideoDTO, ctx));

    placementVideoDTO = TestObjectsFactory.createDefaultLongformPlacementVideoDTO();

    // true, (also set the other video fields with wrong values)
    placementVideoDTO.setLongform(true);
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList =
        new ArrayList<PlacementVideoCompanionDTO>();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO);
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION);

    placementVideoDTO.setCompanions(null);
    // passes as all non-compatible video fields are set to correct values
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void testStreamTypeValidations() {

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();

    // null, longform true
    placementVideoDTO.setStreamType(null);
    placementVideoDTO.setLongform(true);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION);

    // not null, longform false
    placementVideoDTO.setLongform(false);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION);

    placementVideoDTO.setLongform(true);
    // VOD, longform true
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
    // LIVE, longform true
    placementVideoDTO.setStreamType(PlacementVideoStreamType.LIVE);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void testSsaiValidations() {

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();

    // null, longform true
    placementVideoDTO.setSsai(null);
    placementVideoDTO.setLongform(true);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION);

    // not null, longform false
    placementVideoDTO.setLongform(false);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION);

    placementVideoDTO.setLongform(true);
    // UNKNOWN, longform true
    placementVideoDTO.setSsai(PlacementVideoSsai.UNKNOWN);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
    // ASSETS_STICHED_SERVER_SIDE, longform true
    placementVideoDTO.setSsai(PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
    // ALL_CLIENT_SIDE, longform true
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void testPlayerBrandValidations() {

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // null
    placementVideoDTO.setPlayerBrand(null);
    assertTrue(validator.isValid(placementVideoDTO, ctx));

    // not null, longform false
    placementVideoDTO.setPlayerBrand(testPlayerBrand);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION);

    // not null, longform true
    placementVideoDTO = TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    assertTrue(validator.isValid(placementVideoDTO, ctx));

    // string which equals max allowed length
    placementVideoDTO.setPlayerBrand(
        RandomStringUtils.randomAlphanumeric(validator.MAX_PLAYER_BRAND_LENGTH));
    assertTrue(validator.isValid(placementVideoDTO, ctx));

    // string which is more than max allowed length
    placementVideoDTO.setPlayerBrand(
        RandomStringUtils.randomAlphanumeric(validator.MAX_PLAYER_BRAND_LENGTH) + 1);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION);
  }

  @ParameterizedTest
  @MethodSource("data")
  void testPlayerBrandFormatValidations(String playerBrand, boolean expectedResult) {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPlayerBrand(playerBrand);
    validateResult(placementVideoDTO, expectedResult);
  }

  private void validateResult(PlacementVideoDTO placementVideoDTO, boolean expectedResult) {
    if (expectedResult) {
      assertTrue(validator.isValid(placementVideoDTO, ctx));
    } else {
      assertFalse(validator.isValid(placementVideoDTO, ctx));
      verifyValidationMessage(ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION);
    }
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx, atLeastOnce()).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Override
  public void initializeConstraint() {
    lenient().when(annotation.message()).thenReturn(ValidationMessages.WRONG_VALUE);
    lenient()
        .when(annotation.longformViolationMessage())
        .thenReturn(ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION);
    lenient()
        .when(annotation.streamTypeViolationMessage())
        .thenReturn(ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION);
    lenient()
        .when(annotation.playerBrandViolationMessage())
        .thenReturn(ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION);
    lenient()
        .when(annotation.ssaiViolationMessage())
        .thenReturn(ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION);
  }
}
