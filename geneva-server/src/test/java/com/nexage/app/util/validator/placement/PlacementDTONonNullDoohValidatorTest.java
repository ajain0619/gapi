package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.site.Type.DOOH;
import static com.nexage.admin.core.enums.site.Type.MOBILE_WEB;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.NonNullDoohConstraint;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTONonNullDoohValidatorTest extends BaseValidatorTest {

  @Mock NonNullDoohConstraint nonNullDoohConstraint;
  @InjectMocks PlacementDTONonNullDoohValidator validator = new PlacementDTONonNullDoohValidator();

  @Test
  void validDoohSiteWithDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    PlacementDoohDTO doohDTO = new PlacementDoohDTO();
    placementDTO.setDooh(doohDTO);
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(DOOH);
    placementDTO.setSite(siteDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void inValidDoohSiteWithNullDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(DOOH);
    placementDTO.setSite(siteDTO);
    assertFalse(validator.isValid(placementDTO, ctx));
  }

  @Test
  void validNotDoohSiteWithNullDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(MOBILE_WEB);
    placementDTO.setSite(siteDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Override
  public void initializeConstraint() {
    lenient().when(nonNullDoohConstraint.message()).thenReturn(ValidationMessages.WRONG_IS_EMPTY);
  }
}
