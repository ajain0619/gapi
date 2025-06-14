package com.nexage.app.dto.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RuleActionTypeTest {

  @Test
  void whenTypeIsFILTER_andHasNonExistentFilterTye_thenValidateDataThrowsException() {
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> RuleActionType.FILTER.validateData("WHITELIST"));
    assertEquals(ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FILTER_TYPE, exception.getErrorCode());
  }

  @Test
  void whenTypeIsFILTER_andHasValidFilterType_thenValidateDataDoesNotThrowException() {
    RuleActionType.FILTER.validateData("BLOCKLIST");
  }

  @ParameterizedTest
  @ValueSource(strings = {"not big decimal", "0.00", "-1"})
  void shouldThrowExceptionWhenTypeIsFloorAndValueIsInvalid(String inputData) {
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> RuleActionType.FLOOR.validateData(inputData));
    assertEquals(ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FLOOR_PRICE, exception.getErrorCode());
  }

  @Test
  void whenTypeIsFLOOR_andDataIsBigDecimal_thenValidateDataDoesNotThrowException() {
    RuleActionType.FLOOR.validateData("1.00");
  }

  @Test
  void whenTypeIsFLOOR_andIsBigDecimal_thenTranslateDataFromDtoToEntityShouldRoundValueDown() {
    String result = RuleActionType.FLOOR.translateDataFromDtoToEntity("1.123456");
    assertEquals("1.12", result);
  }

  @Test
  void whenTypeIsFLOOR_andIsBigDecimal_thenTranslateDataFromDtoToEntityShouldRoundValueUp() {
    String result = RuleActionType.FLOOR.translateDataFromDtoToEntity("1.987654");
    assertEquals("1.99", result);
  }

  @Test
  void shouldThrowExceptionOnInvalidVisibilityValue() {
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> RuleActionType.VISIBILITY.validateData("1.987654"));
    assertEquals(ServerErrorCodes.SERVER_ACTION_DATA_INVALID_VISIBILITY, exception.getErrorCode());
  }
}
