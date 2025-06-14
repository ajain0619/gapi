package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.AdSourceService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdSourceValidatorTest {

  AdSourceService adSourceService = mock(AdSourceService.class);

  private AdSourceValidator adSourceValidator;

  @BeforeEach
  public void setUp() throws Exception {
    reset(adSourceService);
    adSourceValidator = new AdSourceValidator(adSourceService);
  }

  @Test
  void shouldFailOnBadTierParam() {
    var publisherTier = new PublisherTierDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                adSourceValidator.validateAdSourceAssignedToTiers(
                    null, null, publisherTier, false));

    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, exception.getErrorCode());
  }
}
