package com.nexage.app.services.validation.placementvideo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.placement.PlacementVideoPlayerRequiredValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class PlacementVideoPlayerRequiredValidatorTest {
  @Mock private ConstraintValidatorContext ctx;

  private PlacementVideoPlayerRequiredValidator placementVideoPlayerRequiredValidator =
      new PlacementVideoPlayerRequiredValidator();

  @Test
  void validationPlayerRequiredForDap() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // player height and width not required for DAP request
    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setPlayerHeight(null);
    assertTrue(placementVideoPlayerRequiredValidator.isValid(placementVideoDTO, ctx));

    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setPlayerWidth(null);
    assertTrue(placementVideoPlayerRequiredValidator.isValid(placementVideoDTO, ctx));
  }
}
