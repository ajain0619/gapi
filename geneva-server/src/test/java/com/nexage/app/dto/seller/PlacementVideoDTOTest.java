package com.nexage.app.dto.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementVideoLongformValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlacementVideoDTOTest {
  private static final String testPlayerBrandValid = "test_player12~";
  private static final String testPlayerBrandInvalid = "test_player@";
  private Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  void testPidValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // Null
    placementVideoDTO.setPid(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    placementVideoDTO.setPid(1L);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_NOT_EMPTY, constraintViolations.iterator().next().getMessage());

    // Update
    placementVideoDTO.setVersion(1);
    // Null
    placementVideoDTO.setPid(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    placementVideoDTO.setPid(1L);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testVersionValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // Null
    placementVideoDTO.setVersion(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    placementVideoDTO.setVersion(1);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_NOT_EMPTY, constraintViolations.iterator().next().getMessage());

    // Update
    placementVideoDTO.setPid(1L);
    // Null
    placementVideoDTO.setVersion(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    placementVideoDTO.setVersion(1);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // < 0
    placementVideoDTO.setVersion(-1);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());
  }

  @Test
  void testLinearityValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // LINEAR
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // NON_LINEAR
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testPlayerRequiredValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // true
    placementVideoDTO.setPlayerRequired(true);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // false
    placementVideoDTO.setPlayerRequired(false);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);

    // true
    placementVideoDTO.setPlayerRequired(true);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testPlayerHeightValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // NULL
    placementVideoDTO.setPlayerRequired(false);
    placementVideoDTO.setPlayerHeight(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setPlayerHeight(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL,
        constraintViolations.iterator().next().getMessage());

    // < 1
    placementVideoDTO.setPlayerHeight(0);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // > 9999
    placementVideoDTO.setPlayerHeight(10000);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MAX, constraintViolations.iterator().next().getMessage());

    // valid value
    placementVideoDTO.setPlayerHeight(100);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);

    placementVideoDTO.setPlayerHeight(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL,
        constraintViolations.iterator().next().getMessage());

    // < 1
    placementVideoDTO.setPlayerHeight(0);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // valid value
    placementVideoDTO.setPlayerHeight(100);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testPlayerWidthValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // NULL
    placementVideoDTO.setPlayerRequired(false);
    placementVideoDTO.setPlayerWidth(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setPlayerWidth(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL,
        constraintViolations.iterator().next().getMessage());

    // < 1
    placementVideoDTO.setPlayerWidth(0);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // > 9999
    placementVideoDTO.setPlayerWidth(10000);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MAX, constraintViolations.iterator().next().getMessage());

    // valid value
    placementVideoDTO.setPlayerWidth(100);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);

    placementVideoDTO.setPlayerWidth(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL,
        constraintViolations.iterator().next().getMessage());

    // < 1
    placementVideoDTO.setPlayerWidth(0);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // valid value
    placementVideoDTO.setPlayerWidth(100);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testCompanionsValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // Single Companion
    PlacementVideoCompanionDTO companionDTO = new PlacementVideoCompanionDTO();
    companionDTO.setHeight(320);
    companionDTO.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO);

    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Multiple Companions
    PlacementVideoCompanionDTO companionDTO2 = new PlacementVideoCompanionDTO();
    companionDTO2.setHeight(480);
    companionDTO2.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO2);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Invalid Companions
    PlacementVideoCompanionDTO companionDTO3 = new PlacementVideoCompanionDTO();
    companionDTO3.setHeight(null);
    companionDTO3.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO3);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.removeCompanion(companionDTO3);
    placementVideoDTO.removeCompanion(companionDTO2);
    placementVideoDTO.removeCompanion(companionDTO);

    // Single Companion
    companionDTO.setPid(1L);
    companionDTO.setVersion(1);
    companionDTO.setHeight(320);
    companionDTO.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO);

    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Multiple Companions
    companionDTO2.setPid(2L);
    companionDTO2.setVersion(1);
    companionDTO2.setHeight(480);
    companionDTO2.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO2);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Invalid Companions
    companionDTO3.setPid(3L);
    companionDTO3.setVersion(1);
    companionDTO3.setHeight(null);
    companionDTO3.setWidth(640);
    placementVideoDTO.addCompanion(companionDTO3);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());
  }

  @Test
  void testLongformValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // false
    placementVideoDTO.setLongform(false);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // true, (fails due to constraint violation on longform)
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setCompanions(
        List.of(TestObjectsFactory.createDefaultPlacementVideoCompanionDTO()));
    placementVideoDTO.setStreamType(PlacementVideoStreamType.LIVE);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // true, set the non-compatible video fields

    placementVideoDTO.setCompanions(Lists.newArrayList());
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    // null
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setStreamType(null);
    placementVideoDTO.setSsai(null);

    // false
    placementVideoDTO.setLongform(false);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // true, set the non-compatible video fields
    placementVideoDTO.setCompanions(Lists.newArrayList());
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testStreamTypeValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // null
    placementVideoDTO.setStreamType(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // VOD, fails as longform is false
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // set longform and other required fields
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    // LIVE
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.LIVE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // null (fails due to constraint violation on streamType)
    placementVideoDTO.setStreamType(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // set longform false
    placementVideoDTO.setLongform(false);
    placementVideoDTO.setSsai(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testPlayerBrandValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // null
    placementVideoDTO.setPlayerBrand(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // valid string, fails as longform is false
    placementVideoDTO.setPlayerBrand(testPlayerBrandValid);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // set longform and other required fields
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // set it with invalid characters
    placementVideoDTO.setPlayerBrand(testPlayerBrandInvalid);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // Update
    // remove the invalid characters
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setPlayerBrand(testPlayerBrandValid);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // string which equals max allowed length
    placementVideoDTO.setPlayerBrand(
        RandomStringUtils.randomAlphanumeric(
            PlacementVideoLongformValidator.MAX_PLAYER_BRAND_LENGTH));

    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // string which is more than max allowed length
    placementVideoDTO.setPlayerBrand(
        RandomStringUtils.randomAlphanumeric(
                PlacementVideoLongformValidator.MAX_PLAYER_BRAND_LENGTH)
            + 1);

    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // null
    placementVideoDTO.setPlayerBrand(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testSsaiValidations() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Create
    // null
    placementVideoDTO.setSsai(null);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // ALL_CLIENT_SIDE, fails as longform is false
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // set longform and other required fields
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.LIVE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    // ALL_SERVER_SIDE
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // null (fails due to constraint violation on ssai)
    placementVideoDTO.setSsai(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION,
        constraintViolations.iterator().next().getMessage());

    // set streamType null as well and see two errors are returned
    placementVideoDTO.setStreamType(null);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(2, constraintViolations.size());

    // set longform false
    placementVideoDTO.setLongform(false);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testPlayerRequiredValidationsForDapPlacement() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.INTERSTITIAL);

    // Create
    // NULL
    placementVideoDTO.setPlayerRequired(true);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    placementVideoDTO.setPid(1L);
    placementVideoDTO.setVersion(1);

    // true
    placementVideoDTO.setPlayerRequired(true);
    constraintViolations = validator.validate(placementVideoDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void shouldThrowErrorWhenMultiImpressionBidIsTrueButLongformIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setLongform(false);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_VIDEO_MULTI_IMPRESSION_BID.toString(),
        constraintViolations.iterator().next().getMessage());
  }

  @Test
  void shouldNotThrowErrorWhenMultiImpressionBidIsTrueAndLongformIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void shouldThrowErrorWhenCompetitiveSeparationIsTrueButMultiImpressionBidIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    placementVideoDTO.setCompetitiveSeparation(true);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_VIDEO_COMPETITIVE_SEPARATION.toString(),
        constraintViolations.iterator().next().getMessage());
  }

  @Test
  void shouldNotThrowErrorWhenCompetitiveSeparationIsTrueAndMultiImpressionBidIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(true);
    Set<ConstraintViolation<PlacementVideoDTO>> constraintViolations =
        validator.validate(placementVideoDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());
  }
}
