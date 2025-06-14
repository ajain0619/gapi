package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ReportDefinition;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.ReportDefinitionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.dw.xstream.ReportDefinitionAdapter;
import com.nexage.admin.dw.xstream.XmlReportDefinition;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.report.ReportType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportingServiceImplTest {

  @Mock UserContext userContext;
  @Mock private ReportDefinitionRepository reportDefinitionRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private ReportDefinitionAdapter reportDefAdapter;
  @InjectMocks private ReportingServiceImpl reportingService;

  @Test
  void shouldGetAllReportDefinitions() {
    // given
    var companyPid = 1L;
    var company = new Company();
    var companyType = CompanyType.NEXAGE;
    List<ReportDefinition> reportDefinitions =
        List.of(
            new ReportDefinition("def1", ReportType.BIDDER_ACTIVITY),
            new ReportDefinition("def2", ReportType.BIDDER_SPEND));
    List<XmlReportDefinition> xmlReportDefinitions =
        List.of(
            new XmlReportDefinition("1", "def1", null, null, null, null),
            new XmlReportDefinition("2", "def2", null, null, null, null));
    when(userContext.getCompanyPids()).thenReturn(Set.of(companyPid));
    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    when(userContext.getType()).thenReturn(companyType);
    when(reportDefinitionRepository.findByCompanyTypesContainingOrderByDisplayOrder(companyType))
        .thenReturn(reportDefinitions);
    when(reportDefAdapter.getReportDefObject("def1")).thenReturn(xmlReportDefinitions.get(0));
    when(reportDefAdapter.getReportDefObject("def2")).thenReturn(xmlReportDefinitions.get(1));

    // when
    List<XmlReportDefinition> returnedXmlReportDefs = reportingService.getAllReportDefinitions();

    // then
    assertEquals(xmlReportDefinitions, returnedXmlReportDefs);
  }

  @Test
  void shouldThrowExceptionOnGetAllReportDefinitions() {

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> reportingService.getAllReportDefinitions());

    // then
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundWhenCompanyDoesNotExist() {
    // when
    when(userContext.getCompanyPids()).thenReturn(Set.of(1L));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> reportingService.getAllReportDefinitions());
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldGetAllReportDefinitionsForSeller() {
    // given
    var companyPid = 1L;
    var company = new Company();
    company.setPid(companyPid);
    company.setAdServingEnabled(true);
    company.setRtbRevenueReportEnabled(true);
    var companyType = CompanyType.SELLER;
    List<ReportDefinition> reportDefinitions =
        List.of(
            new ReportDefinition("def1", ReportType.IMPRESSION_GROUP_NET),
            new ReportDefinition("def2", ReportType.AD_SERVER),
            new ReportDefinition("def3", ReportType.RTB_NET_REVENUE_PERFORMANCE));
    List<XmlReportDefinition> xmlReportDefinitions =
        List.of(
            new XmlReportDefinition("1", "def1", null, null, null, null),
            new XmlReportDefinition("2", "def2", null, null, null, null),
            new XmlReportDefinition("2", "def2", null, null, null, null));
    when(userContext.getCompanyPids()).thenReturn(Set.of(companyPid));
    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    when(userContext.getType()).thenReturn(companyType);
    when(reportDefinitionRepository.findByCompanyTypesContainingOrderByDisplayOrder(companyType))
        .thenReturn(reportDefinitions);
    when(reportDefAdapter.getReportDefObject("def1")).thenReturn(xmlReportDefinitions.get(0));
    when(reportDefAdapter.getReportDefObject("def2")).thenReturn(xmlReportDefinitions.get(1));
    when(reportDefAdapter.getReportDefObject("def3")).thenReturn(xmlReportDefinitions.get(2));
    when(siteRepository.existsByCompanyPidAndGroupsEnabledTrueAndStatusGreaterThanEqual(
            any(), any()))
        .thenReturn(true);

    // when
    List<XmlReportDefinition> returnedXmlReportDefs = reportingService.getAllReportDefinitions();

    // then
    assertEquals(xmlReportDefinitions, returnedXmlReportDefs);
  }

  @Test
  void shouldNotGetImpressionGroupReportWhenNoSitesWithGroupsEnabled() {
    // given
    var companyPid = 1L;
    var company = new Company();
    company.setPid(companyPid);
    company.setAdServingEnabled(true);
    company.setRtbRevenueReportEnabled(true);
    var companyType = CompanyType.SELLER;
    List<ReportDefinition> reportDefinitions =
        List.of(new ReportDefinition("def1", ReportType.IMPRESSION_GROUP_NET));
    when(userContext.getCompanyPids()).thenReturn(Set.of(companyPid));
    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    when(userContext.getType()).thenReturn(companyType);
    when(reportDefinitionRepository.findByCompanyTypesContainingOrderByDisplayOrder(companyType))
        .thenReturn(reportDefinitions);
    when(siteRepository.existsByCompanyPidAndGroupsEnabledTrueAndStatusGreaterThanEqual(
            any(), any()))
        .thenReturn(false);

    // when
    List<XmlReportDefinition> returnedXmlReportDefs = reportingService.getAllReportDefinitions();

    // then
    assertEquals(0, returnedXmlReportDefs.size());
  }

  @Test
  void shouldGetReportDefinitionWhenDefinitionExistsAndUserIsAuthorized() {
    // given
    var id = "id1";
    var companyType = CompanyType.NEXAGE;
    var reportDef = new ReportDefinition("def", ReportType.BIDDER_ACTIVITY);
    reportDef.setCompanyTypes(List.of(companyType));
    var xmlReportDef = new XmlReportDefinition("1", "def", null, null, null, null);
    when(reportDefinitionRepository.findById(id)).thenReturn(Optional.of(reportDef));
    when(userContext.getType()).thenReturn(companyType);
    when(reportDefAdapter.getReportDefObject("def")).thenReturn(xmlReportDef);

    // when
    XmlReportDefinition returnedXmlReportDef = reportingService.getReportDefinition(id);

    // then
    assertEquals(xmlReportDef, returnedXmlReportDef);
  }

  @Test
  void shouldThrowExceptionOnGetReportDefinitionWhenDefinitionExistsAndUserIsNotAuthorized() {
    // given
    var id = "id1";
    var companyType = CompanyType.NEXAGE;
    var otherCompanyType = CompanyType.SELLER;
    var reportDef = new ReportDefinition("def", ReportType.BIDDER_ACTIVITY);
    reportDef.setCompanyTypes(List.of(otherCompanyType));
    when(reportDefinitionRepository.findById(id)).thenReturn(Optional.of(reportDef));
    when(userContext.getType()).thenReturn(companyType);

    // throws exception when
    var exception =
        assertThrows(GenevaSecurityException.class, () -> reportingService.getReportDefinition(id));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnGetReportDefinitionWhenDefinitionDoesNotExist() {
    // given
    var id = "id1";
    when(reportDefinitionRepository.findById(id)).thenReturn(Optional.empty());

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> reportingService.getReportDefinition(id));

    // then
    assertEquals(ServerErrorCodes.SERVER_REPORTDEF_NOT_FOUND, exception.getErrorCode());
  }
}
