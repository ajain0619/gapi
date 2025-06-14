package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.validation.sellingrule.AdSizeTargetValidator.AdSizeJson;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdSizeTargetValidatorTest {

  @Mock private ObjectMapper objectMapper;
  @InjectMocks private AdSizeTargetValidator adSizeTargetValidator;

  @Test
  void shouldThrowExceptionOnInitFailure() throws IOException {
    doThrow(new IOException()).when(objectMapper).readValue(any(URL.class), any(JavaType.class));
    assertThrows(IOException.class, () -> adSizeTargetValidator.init());
  }

  @Test
  void shouldNotThrowExceptionWhenMatchesExactly() throws IOException {
    RuleTargetDTO validationTarget = ruleWithAdSizeTargetData("200x200,100x100");
    givenAdSizes(anAdSize("200x200"), anAdSize("100x100"));

    assertDoesNotThrow(() -> adSizeTargetValidator.accept(validationTarget));
  }

  @Test
  void shouldThrowBadRequestWhenAnyAdSizeDoesntMatch() throws IOException {
    RuleTargetDTO validationTarget = ruleWithAdSizeTargetData("200x200,10x10");
    givenAdSizes(anAdSize("150x150"), anAdSize("10x10"));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> adSizeTargetValidator.accept(validationTarget));

    assertEquals(ServerErrorCodes.SERVER_TARGET_INVALID_AD_SIZE, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestOnNullTargetData() throws IOException {
    RuleTargetDTO validationTarget = ruleWithAdSizeTargetData(null);
    givenAdSizes(anAdSize("150x150"), anAdSize("10x10"));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> adSizeTargetValidator.accept(validationTarget));

    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK, exception.getErrorCode());
  }

  private void givenAdSizes(AdSizeJson... adSize) throws IOException {
    doReturn(adSizes(adSize)).when(objectMapper).readValue(any(URL.class), any(JavaType.class));
    adSizeTargetValidator.init();
  }

  private RuleTargetDTO ruleWithAdSizeTargetData(String adSizeTargetData) {
    return RuleTargetDTO.builder()
        .targetType(RuleTargetType.AD_SIZE)
        .data(adSizeTargetData)
        .build();
  }

  private static List<AdSizeJson> adSizes(AdSizeJson... adSizes) {
    return new ArrayList<>(Arrays.asList(adSizes));
  }

  public AdSizeJson anAdSize(String adSizeText) {
    AdSizeJson adSize = new AdSizeJson();
    adSize.setText(adSizeText);
    return adSize;
  }
}
