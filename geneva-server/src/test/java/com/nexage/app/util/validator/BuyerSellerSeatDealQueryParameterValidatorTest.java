package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.nexage.app.util.BuyerSellerSeatDealQueryFieldParameter;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class BuyerSellerSeatDealQueryParameterValidatorTest {

  @Mock private BuyerSellerSeatDealQueryFieldParameterConstraint parameterConstraint;
  @Mock private ConstraintValidatorContext context;

  @InjectMocks private BuyerSellerSeatDealQueryParameterValidator validator;

  @BeforeEach
  void setup() {
    initializeContext();
  }

  @Test
  void shouldMarkEmptyQueryFieldAsInvalid() {
    boolean isValid =
        validator.isValid(prepareValidationMap(new String[0], new String[0][0]), context);
    assertFalse(isValid);
  }

  @Test
  void shouldMarkUnknownFieldAsInvalid() {
    BuyerSellerSeatDealQueryFieldParams queryFieldMap =
        prepareValidationMap("fakeName", "some_val");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertFalse(isValid);
  }

  @Test
  void shouldCorrectlyValidateInputWithSellersAndBuyers() {
    String[] keys = {
      BuyerSellerSeatDealQueryFieldParameter.SELLERS.getName(),
      BuyerSellerSeatDealQueryFieldParameter.DSP_BUYER_SEATS.getName()
    };
    BuyerSellerSeatDealQueryFieldParams queryFieldMap =
        prepareValidationMap(keys, new String[][] {{"1|2", "3|4"}, {"1|2", "5|6"}, {"2|3", "8|9"}});
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  private void initializeContext() {
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }

  private BuyerSellerSeatDealQueryFieldParams prepareValidationMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new BuyerSellerSeatDealQueryFieldParams(map, SearchQueryOperator.AND);
  }

  private BuyerSellerSeatDealQueryFieldParams prepareValidationMap(
      String[] keys, String[][] values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    for (int i = 0; i < keys.length; i++) {
      map.addAll(keys[i], List.of(values[i]));
    }
    return new BuyerSellerSeatDealQueryFieldParams(map, SearchQueryOperator.AND);
  }
}
