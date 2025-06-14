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
import com.nexage.app.util.validator.DoohConstraint;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTODoohValidatorTest extends BaseValidatorTest {

  @Mock private DoohConstraint doohConstraint;

  @InjectMocks private final PlacementDTODoohValidator validator = new PlacementDTODoohValidator();

  @Test
  void shouldReturnTrueWhenValidDoohSiteWithDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    PlacementDoohDTO doohDTO = new PlacementDoohDTO();
    doohDTO.setDefaultImpressionMultiplier(BigDecimal.valueOf(2L));
    placementDTO.setDooh(doohDTO);
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(DOOH);
    placementDTO.setSite(siteDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void shouldReturnTrueWhenDoohSiteAndNullDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(DOOH);
    placementDTO.setSite(siteDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void shouldReturnTrueWhenNotDoohSiteAndNullDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(MOBILE_WEB);
    placementDTO.setSite(siteDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void shouldReturnFalseWhenNotDoohSiteAndNonNullDooh() {
    PlacementDTO placementDTO = new PlacementDTO();
    PlacementDoohDTO doohDTO = new PlacementDoohDTO();
    doohDTO.setDefaultImpressionMultiplier(BigDecimal.valueOf(2L));
    placementDTO.setDooh(doohDTO);
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setType(MOBILE_WEB);
    placementDTO.setSite(siteDTO);
    assertFalse(validator.isValid(placementDTO, ctx));
  }

  @Override
  public void initializeConstraint() {
    lenient().when(doohConstraint.message()).thenReturn("DOOH available for DOOH Sites only");
  }
}
