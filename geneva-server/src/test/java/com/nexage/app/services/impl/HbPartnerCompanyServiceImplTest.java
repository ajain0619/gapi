package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HbPartnerCompanyServiceImplTest {

  @InjectMocks private HbPartnerCompanyServiceImpl hbPartnerCompanyService;

  @Mock private SiteRepository siteRepository;

  HbPartner hbPartner1;
  HbPartner hbPartner2;
  Company company;

  @BeforeEach
  public void setup() {
    hbPartner1 = TestObjectsFactory.createHbPartner();
    hbPartner2 = TestObjectsFactory.createHbPartner();
    company = TestObjectsFactory.createCompany(CompanyType.SELLER);
    company.setHbPartnerCompany(
        Sets.newHashSet(
            TestObjectsFactory.createHbPartnerCompany(company, hbPartner1, "testPibId1"),
            TestObjectsFactory.createHbPartnerCompany(company, hbPartner2, "testPibId2")));
  }

  @Test
  void validateHbPartnerAssociations_testDeletingOneAssociation() {
    when(siteRepository.countSiteAssociationsByCompanyPidAndHbPartnerPids(anyLong(), any()))
        .thenReturn(2);

    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = getHbPartnerDTO(hbPartner1.getPid(), "test_id");
    PublisherDTO publisher =
        PublisherDTO.newBuilder()
            .withHbPartnerAttributes(new HashSet<>(Arrays.asList(hbPartnerAssignmentDTO)))
            .withPid(company.getPid())
            .build();
    publisher.setHbPartnerAttributes(new HashSet<>(Arrays.asList(hbPartnerAssignmentDTO)));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> hbPartnerCompanyService.validateHbPartnerAssociations(company, publisher));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_COMPANY_ASSOCIATION_DELETE_INVALID,
        exception.getErrorCode());
  }

  @Test
  void validateHbPartnerAssociations_testDeletingOneAssociationWithNullHbPartnerDTO() {

    when(siteRepository.countSiteAssociationsByCompanyPidAndHbPartnerPids(anyLong(), any()))
        .thenReturn(2);

    PublisherDTO publisher = PublisherDTO.newBuilder().withPid(company.getPid()).build();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> hbPartnerCompanyService.validateHbPartnerAssociations(company, publisher));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_COMPANY_ASSOCIATION_DELETE_INVALID,
        exception.getErrorCode());
  }

  @Test
  void validateHbPartnerAssociations_testHbPartnerFieldsMissing() {
    PublisherDTO publisher = PublisherDTO.newBuilder().build();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = getHbPartnerDTO(hbPartner1.getPid(), null);
    publisher
        .newBuilder()
        .withHbPartnerAttributes(new HashSet<>(Arrays.asList(hbPartnerAssignmentDTO)))
        .withPid(company.getPid())
        .build();
    publisher.setHbPartnerAttributes(new HashSet<>(Arrays.asList(hbPartnerAssignmentDTO)));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> hbPartnerCompanyService.validateHbPartnerAssociations(company, publisher));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING, exception.getErrorCode());
  }

  private HbPartnerAssignmentDTO getHbPartnerDTO(Long pid, String externalId) {
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(pid);
    hbPartnerAssignmentDTO.setExternalId(externalId);
    return hbPartnerAssignmentDTO;
  }
}
