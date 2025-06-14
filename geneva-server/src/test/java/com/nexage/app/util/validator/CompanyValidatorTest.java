package com.nexage.app.util.validator;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.RegionRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CurrencyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyValidatorTest {

  private static final String DEFAULT_CURRENCY_CODE = "USD";
  @InjectMocks CompanyValidator validator;
  @Mock CurrencyService currencyService;
  @Mock CompanyRepository companyRepository;
  @Mock RegionRepository regionRepository;

  @BeforeEach
  void setUp() {
    lenient().when(currencyService.isCurrencySupported("USD")).thenReturn(Boolean.TRUE);
    lenient().when(currencyService.isCurrencySupported(null)).thenReturn(Boolean.TRUE);
    lenient().when(currencyService.isCurrencySupported("EUR")).thenReturn(Boolean.FALSE);
  }

  @Test
  void shouldThrowExceptionWhenSellerAttributeIsEmptyForSellerCompany() {
    // given
    Company company = new Company();
    company.setType(CompanyType.SELLER);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(company, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_IS_EMPTY, exception.getErrorCode());
  }

  @Test
  void shouldTruncateDescriptionWhenTooLong() {
    // given
    when(regionRepository.existsById(anyLong())).thenReturn(true);
    Company company = new Company();
    company.setType(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    company.setSellerAttributes(sellerAttributes);
    company.setName(RandomStringUtils.randomAlphanumeric(50));
    company.setWebsite(RandomStringUtils.randomAlphanumeric(50));
    company.setDescription(RandomStringUtils.randomAlphanumeric(280));
    company.setRegionId(2L);

    // when & then
    validator.validateCreateCompany(company, DEFAULT_CURRENCY_CODE);
    assertEquals(254, company.getDescription().length());
  }

  @Test
  void shouldThrowExceptionWhenUrlIsNull() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setWebsite(null);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_URL_CANNOT_BE_NULL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingSellerLocalCurrencySupportedAndIsCurrencySupportedFalse() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(currencyService.isCurrencySupported(anyString())).thenReturn(false);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_CURRENCY_NOT_SUPPORTED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenSellerAttributeIsNotEmptyForBuyerCompany() {
    // given
    Company company = new Company();
    company.setType(CompanyType.BUYER);
    company.setSellerAttributes(new SellerAttributes());

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(company, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenNameIsInvalid() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setName(RandomStringUtils.randomAlphanumeric(101));

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NAME_TOO_LONG, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenTypeIsNull() {
    // given
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setType(null);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompanyInput, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_TYPE_CANNOT_BE_NULL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCurrencyIsNull() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setCurrency(null);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_CURRENCY_CANNOT_BE_NULL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenNameIsNull() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setName(null);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NAME_CANNOT_BE_NULL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUrlIsInvalid() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setWebsite(RandomStringUtils.randomAlphanumeric(101));

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(sellerCompany, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_URL_TOO_LONG, exception.getErrorCode());
  }

  @Test
  void shouldThrowDuplicateExceptionOnDuplicateCompanyName() {
    // given
    Company company = new Company();
    company.setName("Test");
    company.setType(CompanyType.SELLER);
    company.setSellerAttributes(new SellerAttributes());
    when(companyRepository.existsByNameAndType(any(), any())).thenReturn(true);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCreateCompany(company, DEFAULT_CURRENCY_CODE));
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_COMPANY_NAME, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithDifferentCurrency() {
    // given
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);
    sellerCompanyInput.setCurrency("EUR");

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    sellerCompanyInput.setRegionId(2L);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateUpdateCompany(sellerCompanyInput, sellerCompanyFromDb));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithStatusDeleted() {
    // given
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);
    sellerCompanyInput.setStatus(Status.DELETED);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    sellerCompanyInput.setRegionId(2L);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateUpdateCompany(sellerCompanyInput, sellerCompanyFromDb));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithNotExistRegion() {
    // given
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    sellerCompanyInput.setRegionId(1L);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateUpdateCompany(sellerCompanyInput, sellerCompanyFromDb));
    assertEquals(ServerErrorCodes.SERVER_REGION_UNKNOWN, exception.getErrorCode());
  }

  private SellerAttributes setSellerAttributes() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(15);
    return sellerAttributes;
  }
}
