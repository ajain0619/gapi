package com.nexage.app.util.validator.deals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.services.CurrencyService;
import com.nexage.app.util.validator.BaseValidatorTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class DirectDealCurrencyValidatorTest extends BaseValidatorTest {

  private static final String USD_CURRENCY = "USD";
  private static final String EUR_CURRENCY = "EUR";

  @Mock CurrencyService currencyService;
  @Mock CompanyRepository companyRepository;
  @Mock DirectDealDTO directDealDTO;
  @Mock DirectDealCurrencyConstraint annotation;
  @InjectMocks DirectDealCurrencyValidator validator;
  @Captor ArgumentCaptor<String> captor;

  @Test
  void whenServiceCreatesDeal_andDtoHasFormula_andCurrencyIsDifferentThanUsd() {
    mockDirectDealDTOWithFormulaAndCurrency();
    mockDealDtoCurrency(EUR_CURRENCY);

    boolean valid = validator.isValid(directDealDTO, ctx);
    validateErrorResult(valid, "Deal currency should be set to USD");
  }

  @Test
  void whenServiceCreatesDeal_andCurrencyIsDifferentThanUsdOrCommon() {
    mockDealDtoCurrency("GBP");
    DealPublisherDTO publisher = mock(DealPublisherDTO.class);
    when(directDealDTO.getSellers()).thenReturn(Lists.newArrayList(publisher));
    when(companyRepository.findUniqueCurrenciesByPids(any()))
        .thenReturn(Lists.newArrayList(EUR_CURRENCY));

    boolean valid = validator.isValid(directDealDTO, ctx);
    validateErrorResult(valid, "Deal currency should be set to USD or common for all publishers");
  }

  @Test
  void whenServiceCreatesDeal_andCurrencyIsNotSupported() {
    String faulty = "faulty";
    mockDealDtoCurrency(faulty);
    lenient().when(currencyService.isCurrencySupported(eq(faulty))).thenReturn(Boolean.FALSE);

    boolean valid = validator.isValid(directDealDTO, ctx);
    validateErrorResult(valid, "Currency is not supported");
  }

  @Test
  void whenServiceCreatesDeal_andPublishersHaveDifferentCurrenciesSet_CurrencyShouldBeUsd() {
    mockPublishers();
    mockDealDtoCurrency(EUR_CURRENCY);
    when(companyRepository.findUniqueCurrenciesByPids(anyListOf(Long.class)))
        .thenReturn(Lists.newArrayList(EUR_CURRENCY, USD_CURRENCY));

    boolean valid = validator.isValid(directDealDTO, ctx);
    validateErrorResult(valid, "Deal currency should be set to USD");
  }

  @Test
  void whenServiceCreatesDeal_andPublishersHaveSameCurrenciesSet_CurrencyShouldBeUsd() {
    mockPublishers();
    mockDealDtoCurrency(EUR_CURRENCY);
    when(companyRepository.findUniqueCurrenciesByPids(anyListOf(Long.class)))
        .thenReturn(Lists.newArrayList(USD_CURRENCY));

    boolean valid = validator.isValid(directDealDTO, ctx);
    validateErrorResult(valid, "Deal currency should be set to USD");
  }

  @Test
  void whenServiceCreatesDeal_andPublishersHaveSameCurrenciesSet_CurrencyShouldBeEur() {
    mockPublishers();
    mockDealDtoCurrency(EUR_CURRENCY);
    when(companyRepository.findUniqueCurrenciesByPids(anyListOf(Long.class)))
        .thenReturn(Lists.newArrayList(EUR_CURRENCY));

    boolean valid = validator.isValid(directDealDTO, ctx);
    assertTrue(valid);
  }

  @Test
  void whenServiceCreatesDeal_andPublishersHaveSameCurrenciesSet_CurrencyShouldBeEurOrUsd() {
    mockPublishers();
    mockDealDtoCurrency(USD_CURRENCY);
    when(companyRepository.findUniqueCurrenciesByPids(anyListOf(Long.class)))
        .thenReturn(Lists.newArrayList(EUR_CURRENCY));

    boolean valid = validator.isValid(directDealDTO, ctx);
    assertTrue(valid);
  }

  private void mockDealDtoCurrency(String curr) {
    lenient().when(directDealDTO.getCurrency()).thenReturn(curr);
    lenient().when(currencyService.isCurrencySupported(eq(curr))).thenReturn(Boolean.TRUE);
    lenient()
        .when(companyRepository.findUniqueCurrenciesByPids(any()))
        .thenReturn(Lists.newArrayList(curr));
  }

  private void mockDirectDealDTOWithFormulaAndCurrency() {
    PlacementFormulaDTO formula = mock(PlacementFormulaDTO.class);
    lenient().when(directDealDTO.getPlacementFormula()).thenReturn(formula);
    mockDealDtoCurrency(USD_CURRENCY);
  }

  private void validateErrorResult(boolean valid, String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(captor.capture());
    assertFalse(valid);
    assertEquals(expectedMessage, captor.getValue());
  }

  private void mockPublishers() {
    DealPublisherDTO publisher = mock(DealPublisherDTO.class);
    List<DealPublisherDTO> publishers = List.of(publisher, publisher);

    when(directDealDTO.getSellers()).thenReturn(publishers);
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(annotation.currency()).thenReturn(USD_CURRENCY);
    lenient().when(annotation.field()).thenReturn("currency");
    validator.initialize(annotation);
  }
}
