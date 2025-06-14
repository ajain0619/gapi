package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionValidatorTest {

  private static final int VERSION = 10;

  @Mock private PositionRepository positionRepository;

  @InjectMocks private PositionValidator positionValidator;

  @Test
  void validateVersion() {
    Position position = new Position();
    position.setVersion(VERSION);
    positionValidator.validateVersion(position, VERSION);
  }

  @Test
  void validateVersion_versionIsDifferent_err() {
    Position position = new Position();
    position.setVersion(VERSION - 1);
    assertThrows(
        StaleStateException.class, () -> positionValidator.validateVersion(position, VERSION));
  }

  @Test
  void validateVersion_positionVersionIsNull_err() {
    Position position = new Position();
    assertThrows(
        StaleStateException.class, () -> positionValidator.validateVersion(position, VERSION));
  }

  @Test
  void validateName_whenPositionNameIsNull_throwBadRequestError() {
    PublisherPositionDTO position = PublisherPositionDTO.builder().build();

    assertThrows(GenevaValidationException.class, () -> positionValidator.validateName(position));
  }

  @Test
  void validateMraidAdvancedTracking_whenMraidAdvancedTrackingisTrue_changeToFalse() {
    Position position = new Position();
    position.setPlacementCategory(PlacementCategory.BANNER);
    position.setMraidAdvancedTracking(true);
    positionValidator.validatePosition(position);
    assertFalse(position.isMraidAdvancedTracking());
  }

  @Test
  void validateName_whenPositionNameExceedsMaxLength_throwBadRequestError() {
    PublisherPositionDTO position = PublisherPositionDTO.builder().build();
    UUIDGenerator uuidGenerator = new UUIDGenerator();
    String invalidName = uuidGenerator.generateUniqueId() + uuidGenerator.generateUniqueId();
    position.setName(invalidName);
    assertThrows(GenevaValidationException.class, () -> positionValidator.validateName(position));
  }
}
