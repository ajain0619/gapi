package com.nexage.app.services.impl.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerCompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.PublisherDefaultRTBProfileAssembler;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.publisher.BasePublisherAssembler;
import com.nexage.app.util.assemblers.publisher.PublisherAssembler;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherCrudServiceImplTest {

  @Mock private CompanyService companyService;
  @Mock private HbPartnerCompanyService hbPartnerCompanyService;
  @Mock private RtbProfileGroupService rtbProfileGroupService;
  @Mock private TransparencyService transparencyService;
  @Mock private UserContext userContext;
  @Mock private RTBProfileService rtbProfileService;
  @Mock private PublisherDefaultRTBProfileAssembler publisherDefaultRTBProfileAssembler;
  @Mock private BasePublisherAssembler publisherAssembler;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;

  private PublisherCrudServiceImpl publisherCrudService;

  @BeforeEach
  void setUp() {
    publisherCrudService =
        new PublisherCrudServiceImpl(
            companyService,
            rtbProfileGroupService,
            transparencyService,
            userContext,
            rtbProfileService,
            hbPartnerCompanyService,
            companyRepository,
            revenueShareUpdateValidator) {
          @Override
          protected PublisherAssembler getAssembler() {
            return publisherAssembler;
          }
        };
  }

  @Test
  void shouldUpdateCompanyRevShareWhenNotAuthorizedShouldThrowException() {
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setVersion(1);
    PublisherDTO publisher = new PublisherDTO();
    publisher.setPid(1L);
    publisher.setVersion(1);
    publisher.setCurrency("EUR");
    publisher.setAttributes(publisherAttributes);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setVersion(1);
    Company company = new Company();
    company.setCurrency("EUR");
    company.setVersion(1);
    company.setSellerAttributes(sellerAttributes);
    when(companyService.getCompany(anyLong())).thenReturn(company);

    when(revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherAttributes))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> publisherCrudService.update(publisher, 1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateCompanyRevShareSucceedsWhenUserHasYieldManagerRole() {
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setVersion(1);
    PublisherDTO publisher = new PublisherDTO();
    publisher.setPid(1L);
    publisher.setVersion(1);
    publisher.setCurrency("EUR");
    publisher.setAttributes(publisherAttributes);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setVersion(1);
    Company company = new Company();
    company.setPid(1L);
    company.setCurrency("EUR");
    company.setVersion(1);
    company.setSellerAttributes(sellerAttributes);
    when(companyService.getCompany(anyLong())).thenReturn(company);
    when(companyService.updateCompanyAndReload(company)).thenReturn(company);

    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SellerAttributes.class), any(PublisherAttributes.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);

    when(publisherAssembler.applyTransparencySettings(any(Company.class), any(PublisherDTO.class)))
        .thenReturn(company);
    when(publisherAssembler.apply(NullableContext.nullableContext, company, publisher))
        .thenReturn(company);
    when(publisherAssembler.applyHbPartnerAttributes(company, publisher)).thenReturn(company);
    when(publisherAssembler.make(NullableContext.nullableContext, company))
        .thenReturn(new PublisherDTO());

    assertNotNull(publisherCrudService.update(publisher, 1L));
  }

  @Test
  void shouldUpdateCompanyWithCurrencyChangeShouldThrowGenevaValidationException() {
    PublisherDTO publisher =
        PublisherDTO.newBuilder().withVersion(1).withPid(2L).withCurrency("JPY").build();

    Company company = new Company();
    company.setCurrency("EUR");
    company.setVersion(1);

    when(companyService.getCompany(2L)).thenReturn(company);

    assertThrows(GenevaValidationException.class, () -> publisherCrudService.update(publisher, 1L));
  }

  @Test
  void shouldUpdateCompanyExistingNameShouldThrowException() {
    Company mockCompany = mock(Company.class);
    when(mockCompany.getVersion()).thenReturn(1);
    when(companyService.getCompany(anyLong())).thenReturn(mockCompany);

    PublisherDTO publisher =
        PublisherDTO.newBuilder().withVersion(1).withPid(2L).withCurrency("JPY").build();
    assertThrows(GenevaValidationException.class, () -> publisherCrudService.update(publisher, 1L));
  }

  @Test
  void shouldUpdateCompanySellerAttributesShouldThrowException() {
    Company mockCompany = mock(Company.class);
    when(mockCompany.getVersion()).thenReturn(1);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    when(mockCompany.getSellerAttributes()).thenReturn(sellerAttributes);
    when(mockCompany.getCurrency()).thenReturn("JPY");
    when(companyService.getCompany(anyLong())).thenReturn(mockCompany);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            Mockito.isA(SellerAttributes.class), Mockito.isA(PublisherAttributes.class)))
        .thenReturn(true);

    PublisherDTO publisher =
        PublisherDTO.newBuilder().withVersion(1).withPid(2L).withCurrency("JPY").build();

    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.valueOf(0.35));
    publisher.setAttributes(publisherAttributes);

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> publisherCrudService.update(publisher, 1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenVersionsDoNotMatchOnUpdate() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setVersion(1);
    smartExchangeAttributes.setSellerAttributes(sellerAttributes);
    sellerAttributes.setSmartExchangeAttributes(smartExchangeAttributes);
    Company company = TestObjectsFactory.createCompany(CompanyType.SELLER);
    company.setVersion(1);
    company.setSellerAttributes(sellerAttributes);

    when(companyService.getCompany(anyLong())).thenReturn(company);

    SmartExchangeAttributesDTO smartExchangeAttributesDTO =
        SmartExchangeAttributesDTO.newBuilder().withVersion(0).build();
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withSmartExchangeAttributes(smartExchangeAttributesDTO)
            .build();

    PublisherDTO publisher =
        PublisherDTO.newBuilder()
            .withVersion(1)
            .withPid(2L)
            .withAttributes(publisherAttributes)
            .withCurrency("USD")
            .build();

    assertThrows(StaleStateException.class, () -> publisherCrudService.update(publisher, 1L));
  }
}
