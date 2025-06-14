package com.nexage.app.util.validator.deals;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Strings;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.services.CurrencyService;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Validator for currency in direct deals */
@Component
public class DirectDealCurrencyValidator
    extends BaseValidator<DirectDealCurrencyConstraint, DirectDealDTO> {

  private final CurrencyService currencyService;
  private final CompanyRepository companyRepository;
  private String defaultCurrency;
  private String defaultCurrencyMessage;

  public DirectDealCurrencyValidator(CurrencyService service, CompanyRepository companyRepository) {
    this.currencyService = service;
    this.companyRepository = companyRepository;
  }

  @Override
  public void initialize(DirectDealCurrencyConstraint annotation) {
    super.initialize(annotation);
    defaultCurrency = getAnnotation().currency();
    defaultCurrencyMessage =
        String.format(
            DealValidationMessages.DEAL_CURRENCY_SHOULD_BE_DEFAULT, getAnnotation().currency());
  }

  @Override
  public boolean isValid(DirectDealDTO dealDto, ConstraintValidatorContext context) {
    if (Strings.isNullOrEmpty(dealDto.getCurrency())) {
      return true;
    }

    if (StringUtils.length(dealDto.getCurrency()) != 3
        || !currencyService.isCurrencySupported(dealDto.getCurrency())) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), DealValidationMessages.DEAL_CURRENCY_NOT_SUPPORTED);
      return false;
    }

    // if formula is entered we should default currency should be set
    if (dealDto.getPlacementFormula() != null) {
      if (!dealDto.getCurrency().equals(defaultCurrency)) {
        ValidationUtils.addConstraintMessage(
            context, getAnnotation().field(), defaultCurrencyMessage);
        return false;
      }
    }

    if (CollectionUtils.isNotEmpty(dealDto.getSellers())) {
      List<Long> pids =
          dealDto.getSellers().stream().map(DealPublisherDTO::getPublisherPid).collect(toList());
      List<String> currenciesByPids = companyRepository.findUniqueCurrenciesByPids(pids);
      if (currenciesByPids.isEmpty()) {
        ValidationUtils.addConstraintMessage(
            context, getAnnotation().field(), ValidationMessages.COMPANY_NOT_FOUND);
        return false;
      }

      if (currenciesByPids.size() > 1) {
        if (!defaultCurrency.equals(dealDto.getCurrency())) {
          ValidationUtils.addConstraintMessage(
              context, getAnnotation().field(), defaultCurrencyMessage);
          return false;
        }
      } else {
        String commonCurrency = currenciesByPids.get(0);
        if (defaultCurrency.equals(commonCurrency)) {
          if (!defaultCurrency.equals(dealDto.getCurrency())) {
            ValidationUtils.addConstraintMessage(
                context, getAnnotation().field(), defaultCurrencyMessage);
            return false;
          }
        } else if (!Arrays.asList(commonCurrency, defaultCurrency)
            .contains(dealDto.getCurrency())) {
          String message =
              String.format(
                  DealValidationMessages.DEAL_CURRENCY_SHOULD_BE_DEFAULT_OR_COMMON,
                  getAnnotation().currency());
          ValidationUtils.addConstraintMessage(context, getAnnotation().field(), message);
          return false;
        }
      }
    }

    return true;
  }
}
