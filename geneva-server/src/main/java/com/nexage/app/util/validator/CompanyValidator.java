package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.RegionRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CurrencyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class CompanyValidator {

  private final CurrencyService currencyService;
  private final CompanyRepository companyRepository;
  private final RegionRepository regionRepository;

  /**
   * Validates the company can be created. Validation checks include duplicate names, valid regions,
   * and company type.
   *
   * @param company to be validated
   * @param defaultCurrencyCode to be used for currency validation
   */
  public void validateCreateCompany(Company company, String defaultCurrencyCode) {
    if (CompanyType.SELLER.equals(company.getType()) && company.getSellerAttributes() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_IS_EMPTY);
    }
    if (CompanyType.BUYER.equals(company.getType()) && company.getSellerAttributes() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_NOT_ALLOWED);
    }

    if (isCompanyTypeSupportLocalCurrencies(company)
            && !currencyService.isCurrencySupported(company.getCurrency())
        || (!isCompanyTypeSupportLocalCurrencies(company)
            && !isDefaultCurrencySet(company, defaultCurrencyCode))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_CURRENCY_NOT_SUPPORTED);
    }

    checkDuplicatedCompanyName(company);
    validateCompany(company);
  }

  /**
   * Validates the company can be updated. Validation checks include duplicate names, currency
   * changes, and valid regions.
   *
   * @param company to be validated
   * @param companyInDb being updated
   */
  public void validateUpdateCompany(Company company, Company companyInDb) {

    if (company.getStatus() == Status.DELETED) {
      log.error("Cannot set company to deleted status.");
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    if (!StringUtils.defaultString(companyInDb.getName()).equalsIgnoreCase(company.getName())) {
      checkDuplicatedCompanyName(company);
    }

    if (!Objects.equals(companyInDb.getCurrency(), company.getCurrency())) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    validateCompany(company);
  }

  private boolean isDefaultCurrencySet(Company company, String defaultCurrencyCode) {
    return defaultCurrencyCode.equals(company.getCurrency());
  }

  private void checkDuplicatedCompanyName(Company company) {
    if (companyRepository.existsByNameAndType(company.getName(), company.getType())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_COMPANY_NAME);
    }
  }

  private void validateRegionExist(Long regionId) {
    if (regionId != null && !regionRepository.existsById(regionId)) {
      log.error("Cannot set region to {}, value is unexpected.", regionId);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REGION_UNKNOWN);
    }
  }

  private boolean isCompanyTypeSupportLocalCurrencies(Company company) {
    return CompanyType.BUYER.equals(company.getType())
        || CompanyType.SELLER.equals(company.getType());
  }

  private void validateCompany(Company company) {

    validateRegionExist(company.getRegionId());

    if (StringUtils.isBlank(company.getName())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NAME_CANNOT_BE_NULL);
    }
    if (company.getType() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_TYPE_CANNOT_BE_NULL);
    }
    if (StringUtils.isBlank(company.getWebsite())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_URL_CANNOT_BE_NULL);
    }
    if (StringUtils.isBlank(company.getCurrency())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_CURRENCY_CANNOT_BE_NULL);
    }
    if (company.getName().length() > 100) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NAME_TOO_LONG);
    }
    if (company.getWebsite().length() > 100) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_URL_TOO_LONG);
    }
    validateDescription(company);
  }

  private void validateDescription(Company company) {
    if (company != null) {
      company.setDescription(truncateDescription(company.getDescription()));
    }
  }

  private String truncateDescription(String desc) {
    if (StringUtils.isNotBlank(desc) && desc.length() > 255) {
      desc = StringUtils.substring(desc, 0, 254);
    }
    return desc;
  }
}
