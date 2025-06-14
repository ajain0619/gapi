package com.nexage.app.services.impl.publisher;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerCompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.publisher.ExternalPublisherAssembler;
import com.nexage.app.util.assemblers.publisher.PublisherAssembler;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalPublisherCrudServiceImplTest {

  @Mock private CompanyService companyService;
  @Mock private RtbProfileGroupService rtbProfileGroupService;
  @Mock private TransparencyService transparencyService;
  @Mock private UserContext userContext;
  @Mock private RTBProfileService rtbProfileService;
  @Mock private HbPartnerCompanyService hbPartnerCompanyService;
  @Mock private CompanyRepository companyRepository;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;
  @Mock private ExternalPublisherAssembler externalPublisherAssembler;

  private ExternalPublisherCrudServiceImpl externalPublisherCrudService;

  @BeforeEach
  void setUp() {
    externalPublisherCrudService =
        new ExternalPublisherCrudServiceImpl(
            companyService,
            rtbProfileGroupService,
            transparencyService,
            userContext,
            rtbProfileService,
            hbPartnerCompanyService,
            companyRepository,
            revenueShareUpdateValidator,
            externalPublisherAssembler);
  }

  @Test
  void shouldReturnAssembler() {
    PublisherAssembler assembler = externalPublisherCrudService.getAssembler();
    assertNotNull(assembler);
    assertTrue(assembler instanceof ExternalPublisherAssembler);
  }

  @Test
  void shouldFailOnCreate() {
    UnsupportedOperationException exception =
        assertThrows(
            UnsupportedOperationException.class,
            () -> externalPublisherCrudService.create(mock(PublisherDTO.class)));
    assertNotNull(exception);
  }

  @Test
  void shouldFailOnDelete() {
    Long entityIdentifier = new Random().nextLong();
    UnsupportedOperationException exception =
        assertThrows(
            UnsupportedOperationException.class,
            () -> externalPublisherCrudService.delete(entityIdentifier));
    assertNotNull(exception);
  }
}
