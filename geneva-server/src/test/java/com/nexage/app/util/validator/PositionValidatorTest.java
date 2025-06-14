package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.PositionValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionValidatorTest {
  @Mock private PositionRepository positionRepository;

  @InjectMocks private PositionValidator positionValidator;

  private Position position;

  @BeforeEach
  public void setUp() {
    position = new Position();
    position.setPlacementCategory(PlacementCategory.NATIVE);
  }

  @Test
  void testPositionValidatorInvalidHeight() {
    position.setHeight(-10);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));

    assertEquals(ServerErrorCodes.SERVER_INVALID_HEIGHT, exception.getErrorCode());
  }

  @Test
  void testPositionValidatorInvalidWidth() {
    position.setWidth(-10);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));

    assertEquals(ServerErrorCodes.SERVER_INVALID_WIDTH, exception.getErrorCode());
  }

  @Test
  void testPositionInvalidVideoPlaybackMethod() {
    position.setVideoSupport(VideoSupport.VIDEO);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);

    position.setVideoPlaybackMethod("7");

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));

    assertEquals(ServerErrorCodes.SERVER_INVALID_PLAYBACK_METHOD, exception.getErrorCode());
  }

  @Test
  void testPositionNoVideoAttributesFail() {
    position.setVideoSupport(VideoSupport.BANNER);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);

    position.setVideoLinearity(VideoLinearity.LINEAR);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));

    assertEquals(ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_ATTRIBUTES, exception.getErrorCode());
  }

  @Test
  void testValidatePosition_InvalidImpressionTypeHandling_false() {
    Site site = new Site();
    site.setType(Type.DESKTOP);
    position.setSite(site);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    position.setVideoSupport(VideoSupport.BANNER);
    position.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));

    assertEquals(
        ServerErrorCodes.SERVER_INVALID_IMPRESSION_TYPE_HANDLING, exception.getErrorCode());
  }

  @Test
  void testValidatePosition_ValidImpressionTypeHandlingIsBasedOnInboundRequest_true() {
    Site site = new Site();
    site.setType(Type.DESKTOP);
    position.setSite(site);
    position.setPlacementCategory(PlacementCategory.BANNER);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    position.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);

    assertDoesNotThrow(() -> positionValidator.validatePosition(position));
  }

  @Test
  void testValidatePosition_ValidImpressionTypeHandlingIsBasedOnPlacementConfig_true() {
    Site site = new Site();
    site.setType(Type.DESKTOP);
    position.setSite(site);
    position.setPlacementCategory(PlacementCategory.BANNER);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    position.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG);

    assertDoesNotThrow(() -> positionValidator.validatePosition(position));
  }

  @Test
  void shouldThrowExceptionOnInvalidMaxDuration() {
    setPositionWithVideoAttributes();
    position.setVideoMaxdur(-1);

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(ServerErrorCodes.SERVER_INVALID_VIDEO_MAXDUR, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnInvalidStartDelay() {
    setPositionWithVideoAttributes();
    position.setVideoStartDelay(-3);

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(ServerErrorCodes.SERVER_INVALID_VIDEO_STARTDELAY, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnInvalidVideoSkipThreshold() {
    setPositionWithVideoAttributes();
    position.setVideoSkipThreshold(-1);

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPTHRESHOLD, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnInvalidVideoSkipOffset() {
    setPositionWithVideoAttributes();
    position.setVideoSkipOffset(-1);

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPOFFSET, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnInvalidPositionAliasName() {
    setPositionWithVideoAttributes();
    position.setPositionAliasName(
        "very long name which is not valid since the limit of allowed length is only 45");

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(
        ServerErrorCodes.SERVER_POSITION_ALIAS_NAME_LENGTH_EXCEEDED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnDuplicatePositionAliasName() {
    setPositionWithVideoAttributes();
    position.setPositionAliasName("test-position");
    position.setPid(1L);
    var positionFromDB = new Position();
    positionFromDB.setPid(2L);
    positionFromDB.setPositionAliasName("test-position");
    when(positionRepository.findByPositionAliasName(any())).thenReturn(List.of(positionFromDB));

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> positionValidator.validatePosition(position));
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_POSITION_ALIAS_NAME, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenPublisherPositionAliasNameTooLong() {
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName(
                "very long name which is not valid since the limit of allowed length is only 45")
            .build();

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> positionValidator.validatePublisherPositionAliasName(publisherPosition));
    assertEquals(
        ServerErrorCodes.SERVER_POSITION_ALIAS_NAME_LENGTH_EXCEEDED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenPublisherPositionAliasNameIsNotUnique() {
    var positionFromDB = new Position();
    positionFromDB.setPid(2L);
    positionFromDB.setPositionAliasName("test-position");
    when(positionRepository.findByPositionAliasName(any())).thenReturn(List.of(positionFromDB));
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder().withPid(1L).withPositionAliasName("test-position").build();

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> positionValidator.validatePublisherPositionAliasName(publisherPosition));
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_POSITION_ALIAS_NAME, exception.getErrorCode());
  }

  @Test
  void WhenPublisherPositionAliasNameIsUnique() {
    when(positionRepository.findByPositionAliasName(any())).thenReturn(Collections.emptyList());

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder().withPid(1L).withPositionAliasName("test-position").build();

    assertDoesNotThrow(
        () -> positionValidator.validatePublisherPositionAliasName(publisherPosition));
  }

  private void setPositionWithVideoAttributes() {
    Site site = new Site();
    site.setType(Type.DESKTOP);
    position.setSite(site);
    position.setPlacementCategory(PlacementCategory.BANNER);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    position.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
  }
}
