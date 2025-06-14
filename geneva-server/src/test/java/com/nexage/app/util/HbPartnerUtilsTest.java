package com.nexage.app.util;

import static com.nexage.app.util.HbPartnerUtils.ResponseConfigOptions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HbPartnerUtilsTest {

  @Spy private CustomObjectMapper objectMapper = new CustomObjectMapper();
  @InjectMocks HbPartnerUtils hbPartnerUtils;

  @Test
  void shouldLoadAllPartnerValues() {
    List<String> partnerHandler = hbPartnerUtils.getPartnersHandlers();
    assertEquals(5, partnerHandler.size());
  }

  @ParameterizedTest()
  @ValueSource(strings = {"appNexus"})
  void shouldAcceptValidPartnerName(String input) {
    assertTrue(hbPartnerUtils.isValidPartnerName(input));
  }

  @Test
  void shouldReturnInvalidPartnerName() {
    assertFalse(hbPartnerUtils.isValidPartnerName("googl"));
  }

  @Test
  void testPartnersResponseConfigLoad() {
    List<ResponseConfigOptions> responseConfigOptions =
        hbPartnerUtils.getResponseConfigKeysAndOptions();
    assertNotNull(responseConfigOptions);
  }
}
