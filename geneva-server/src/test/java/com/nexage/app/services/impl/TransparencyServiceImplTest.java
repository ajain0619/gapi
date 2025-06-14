package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.dto.transparency.TransparencySettingsEntity;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransparencyServiceImplTest {

  @Mock UserContext userContext;
  @Mock CompanyRepository companyRepository;

  @InjectMocks TransparencyServiceImpl transparencyService;

  @Test
  void isTransparencySettingsEqualsPrevValues() {
    String aliasName = "nameAlias";
    Long aliasId = 123L;

    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(null, null, null, aliasName));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(null, null, aliasId, null));
    assertTrue(transparencyService.isTransparencySettingsEqualsPrevValues(null, 0, null, null));
    assertFalse(transparencyService.isTransparencySettingsEqualsPrevValues(null, null, null, null));

    TransparencyMode transparencyMode = TransparencyMode.Aliases;
    TransparencySettingsDTO transparencySettings =
        new TransparencySettingsDTO(transparencyMode, aliasId, aliasName);
    assertFalse(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), aliasId, aliasName));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, null, aliasId, aliasName));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), null, aliasName));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), aliasId, null));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), aliasId, aliasName + "1"));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), aliasId + 1, aliasName));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, 0, aliasId, aliasName));

    transparencyMode = TransparencyMode.None;
    transparencySettings = new TransparencySettingsDTO(transparencyMode, null, null);
    assertFalse(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), null, null));
    assertTrue(
        transparencyService.isTransparencySettingsEqualsPrevValues(
            transparencySettings, transparencyMode.asInt(), 11113L, null));
  }

  @Test
  void validateTransparencySettings() {
    Long publisherId = 111L;
    RTBProfile rtbProfile = null;
    Mockito.when(userContext.isNexageUser()).thenReturn(true);

    TransparencySettingsDTO transparencySettings = new TransparencySettingsDTO();
    // mode= null - impossible
    GenevaValidationException exp =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              transparencyService.validateTransparencySettingsForRTBProfile(
                  publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);
            });
    assertEquals(ServerErrorCodes.SERVER_TRANSPARENCY_SITE_MODE, exp.getErrorCode());

    // mode = Aliasess. Name alias is mandatory
    transparencySettings.setTransparencyMode(TransparencyMode.Aliases);
    transparencySettings.setNameAlias("nameAlias");
    transparencyService.validateTransparencySettingsForRTBProfile(
        publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);

    // mode = Aliasess. Name alias is mandatory.
    transparencySettings.setNameAlias(null);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              transparencyService.validateTransparencySettingsForRTBProfile(
                  publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);
            });
    assertEquals(
        ServerErrorCodes.SERVER_TRANSPARENCY_SITE_NAME_ALIAS_ERROR, exception.getErrorCode());

    // mode = None. Name alias must be null.
    transparencySettings.setTransparencyMode(TransparencyMode.None);
    transparencySettings.setNameAlias(null);
    transparencyService.validateTransparencySettingsForRTBProfile(
        publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);

    // mode = None. Name alias must be null.
    transparencySettings.setTransparencyMode(TransparencyMode.None);
    transparencySettings.setNameAlias("nameAlias");
    GenevaValidationException gve =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              transparencyService.validateTransparencySettingsForRTBProfile(
                  publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);
            });
    assertEquals(ServerErrorCodes.SERVER_TRANSPARENCY_SITE_NAME_ALIAS_ERROR, gve.getErrorCode());

    // mode = RealName. Name alias must be null.
    transparencySettings.setTransparencyMode(TransparencyMode.RealName);
    transparencySettings.setNameAlias(null);
    transparencyService.validateTransparencySettingsForRTBProfile(
        publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);

    // mode = RealName. Name alias must be null.

    transparencySettings.setTransparencyMode(TransparencyMode.RealName);
    transparencySettings.setNameAlias("nameAlias");
    GenevaValidationException e =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              transparencyService.validateTransparencySettingsForRTBProfile(
                  publisherId, rtbProfile, transparencySettings, TransparencySettingsEntity.SITE);
            });
    assertEquals(ServerErrorCodes.SERVER_TRANSPARENCY_SITE_NAME_ALIAS_ERROR, e.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundWhenCompanyDoesNotExist() {
    // when
    Mockito.when(companyRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                transparencyService.validateTransparencySettingsForRTBProfile(
                    1L, null, null, TransparencySettingsEntity.SITE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }
}
