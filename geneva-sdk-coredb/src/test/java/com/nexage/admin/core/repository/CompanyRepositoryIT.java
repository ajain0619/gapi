package com.nexage.admin.core.repository;

import static com.nexage.admin.core.util.TestUtil.DB1_PREFIX;
import static com.nexage.admin.core.util.TestUtil.TEST_PREFIX;
import static com.nexage.admin.core.util.TestUtil.getTestCompany;
import static com.nexage.admin.core.util.TestUtil.getValue;
import static com.nexage.admin.core.util.TestUtil.validateCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.BaseModel;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyMdmId;
import com.nexage.admin.core.model.MdmId;
import com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation;
import com.nexage.admin.core.projections.AllSeatHolderMetaDataReturnProjection;
import com.nexage.admin.core.projections.BuyerMetaDataForCompanyProjection;
import com.nexage.admin.core.projections.SearchSummaryProjection;
import com.nexage.admin.core.projections.SellerMetaDataForCompanyProjection;
import com.nexage.admin.core.specification.CompanySpecification;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/company-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class CompanyRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired CompanyRepository companyRepository;
  @Autowired UserRepository userRepository;
  @Autowired SiteRepository siteRepository;
  @Autowired TagRepository tagRepository;

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Test
  void shouldCreateCompany() {
    // given
    Company company = getTestCompany();

    // when
    company = companyRepository.save(company);

    // then
    validateCompany(company, TEST_PREFIX, false);
  }

  @Test
  void shouldNotCreateDuplicateCompany() {
    // given
    String companyName = getValue("db1.company.name");
    CompanyType companyType = CompanyType.valueOf(getValue("db1.company.type"));
    Company company = new Company(companyName, companyType);
    company.setWebsite("www.test.com");
    company.setDescription("duplicate company test");
    company.setStatus(Status.ACTIVE);

    // when / then
    assertThrows(
        DataIntegrityViolationException.class, () -> companyRepository.saveAndFlush(company));
    assertThrows(
        AssertionFailure.class,
        () -> companyRepository.existsByNameAndType(companyName, companyType));
  }

  @Test
  void shouldUpdateCompany() {
    // given
    Company company =
        companyRepository
            .findById(1L)
            .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    company.setName(getValue("test.company.name"));
    company.setType(CompanyType.valueOf(getValue("test.company.type")));
    company.setSalesforceId("Sales1");
    company.setDhReportingId(getValue("test.company.dhReportingId"));

    // when
    Company updatedCompany = companyRepository.save(company);

    // then
    assertNotNull(updatedCompany);
    assertEquals(company.getId(), updatedCompany.getId());
    assertEquals(getValue("test.company.name"), updatedCompany.getName());
    assertEquals(getValue("test.company.salesforceId"), updatedCompany.getSalesforceId());
    assertEquals(CompanyType.valueOf(getValue("test.company.type")), updatedCompany.getType());
    assertEquals(getValue("test.company.dhReportingId"), updatedCompany.getDhReportingId());
  }

  @Test
  void shouldFindAllCompanies() {
    // when
    List<Company> list = companyRepository.findAll();

    // then
    assertEquals(9, list.size());
  }

  @Test
  void shouldFindByPidIn() {
    // when
    List<Company> list = companyRepository.findByPidIn(Set.of(1L, 2L));

    // then
    assertEquals(2, list.size());
  }

  @Test
  void shouldNotFindCompanyByInvalidNameAndType() {
    assertFalse(
        companyRepository.existsByNameAndType(
            getValue("db1.company.name"), CompanyType.valueOf(getValue("db2.company.type"))));
  }

  @Test
  void shouldFindCompanyByPid() {
    // when
    Company company = companyRepository.findById(1L).orElse(null);

    // then
    validateCompany(company, DB1_PREFIX, true);
  }

  @Test
  void shouldReturnEmptyOptionalWhenFindByInvalidPid() {
    assertTrue(companyRepository.findById(2001L).isEmpty());
  }

  @Test
  void shouldFindAllByCompanyType() {
    assertEquals(1, companyRepository.findByType(CompanyType.NEXAGE).size());
    assertEquals(4, companyRepository.findByType(CompanyType.SELLER).size());
  }

  @Test
  void shouldFindCompanyPidsBySellerSeatPid() {
    // given
    Long sellerSeatId = 1L;

    // when
    List<Long> returnedList = companyRepository.findCompanyPidsBySellerSeatPid(sellerSeatId);

    // then
    assertEquals(3, returnedList.size());
  }

  @Test
  void shouldCountCompaniesAssociatedToHbPartners() throws Exception {
    assertEquals(
        2,
        (int) companyRepository.countCompaniesAssociatedToHbPartners(Arrays.asList(1L, 2L)),
        "invalid count of companies");
  }

  @Test
  void shouldAggregateSellerMetricsByCompanyPids() {
    Page<CompanyMetricsAggregation> companyMetricsAggregations =
        companyRepository.aggregateNonNexageMetricsByCompanies(
            getDate("2020-03-01"),
            getDate("2020-03-02"),
            Sets.newHashSet(1L, 2L),
            PageRequest.of(0, 10));

    assertEquals(1, companyMetricsAggregations.getTotalElements());
    List<CompanyMetricsAggregation> content = companyMetricsAggregations.getContent();
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(1L)),
        "nexage company is not included");
    assertTrue(content.stream().anyMatch(cma -> cma.getPid().equals(2L)), "seller is included");
    assertNull(content.get(0).getTotalEcpm());
    assertNull(content.get(0).getTotalRpm());
    assertNull(content.get(0).getTotalRevenue());
    assertNull(content.get(0).getVerizonRevenue());
  }

  @Test
  void shouldAggregateSellerMetricsByNameAndCompanyPids() {
    Page<CompanyMetricsAggregation> companyMetricsAggregations =
        companyRepository.aggregateNonNexageMetricsByNameAndCompanies(
            getDate("2020-03-01"),
            getDate("2020-03-02"),
            "Publisher",
            Sets.newHashSet(1L, 2L, 3L),
            PageRequest.of(0, 10));

    assertEquals(1, companyMetricsAggregations.getTotalElements());
    List<CompanyMetricsAggregation> content = companyMetricsAggregations.getContent();
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(1L)),
        "nexage company is not included");
    assertTrue(
        content.stream().anyMatch(cma -> cma.getPid().equals(2L)),
        "seller with name Publisher is included");
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(3L)),
        "seller with name Seller is NOT");
    assertNull(content.get(0).getTotalEcpm());
    assertNull(content.get(0).getTotalRpm());
    assertNull(content.get(0).getTotalRevenue());
    assertNull(content.get(0).getVerizonRevenue());
  }

  @Test
  void shouldAggregateSellerMetricsBySellerSeat() throws ParseException {
    final long sellerSeatPid = 1L;
    Page<CompanyMetricsAggregation> companyMetricsAggregations =
        companyRepository.aggregateMetricsBySellerSeatPid(
            getDate("2020-03-01"), getDate("2020-03-02"), sellerSeatPid, PageRequest.of(0, 10));

    assertEquals(3, companyMetricsAggregations.getTotalElements());
    List<CompanyMetricsAggregation> content = companyMetricsAggregations.getContent();
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(1L)),
        "nexage company is not included");
    assertTrue(
        content.stream().anyMatch(cma -> cma.getPid().equals(2L)),
        "seller with name Publisher is included");
    assertTrue(
        content.stream().anyMatch(cma -> cma.getPid().equals(3L)),
        "seller with name Seller is NOT");
    assertNotNull(getCompanyMetricsAggregationForPublisher1(content).getTotalEcpm());
    assertNotNull(getCompanyMetricsAggregationForPublisher1(content).getTotalRpm());
    assertNotNull(getCompanyMetricsAggregationForPublisher1(content).getTotalRevenue());
    assertNotNull(getCompanyMetricsAggregationForPublisher1(content).getVerizonRevenue());
  }

  @Test
  void shouldAggregateSellerMetricsByNameAndSellerSeat() {
    final long sellerSeatPid = 1L;
    Page<CompanyMetricsAggregation> companyMetricsAggregations =
        companyRepository.aggregateMetricsByNameAndSellerSeat(
            getDate("2020-03-01"),
            getDate("2020-03-02"),
            "Publisher",
            sellerSeatPid,
            PageRequest.of(0, 10));

    assertEquals(1, companyMetricsAggregations.getTotalElements());
    List<CompanyMetricsAggregation> content = companyMetricsAggregations.getContent();
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(1L)),
        "nexage company is not included");
    assertTrue(
        content.stream().anyMatch(cma -> cma.getPid().equals(2L)),
        "seller with name Publisher is included");
    assertFalse(
        content.stream().anyMatch(cma -> cma.getPid().equals(3L)),
        "seller with name Seller is NOT");
    assertNotNull(content.get(0).getTotalEcpm());
    assertNotNull(content.get(0).getTotalRpm());
    assertNotNull(content.get(0).getTotalRevenue());
    assertNotNull(content.get(0).getVerizonRevenue());
  }

  @Test
  void shouldReturnSellerSeatPidAttachedToCompany() {
    // given
    Long sellerId = 1L;

    // when
    Long returnedSellerSeatPid = companyRepository.findSellerSeatIdByCompanyPid(sellerId);

    // then
    assertEquals(2, (long) returnedSellerSeatPid);
  }

  @Test
  void shouldFindAllIdsBySellerSeatPid() {
    Long sellerSeatPid = 1L;
    Set<Long> companiesInSellerSeat = companyRepository.findAllIdsBySellerSeatPid(sellerSeatPid);
    assertEquals(ImmutableSet.of(2L, 3L, 6L), companiesInSellerSeat);
  }

  @Test
  void shouldReturnSellerSeatIdForExistingCompany() {
    // given
    Long companyPid = 2L;

    // when
    Optional<Long> sellerSeatIdOptional = companyRepository.findSellerSeatIdByPid(companyPid);

    // then
    assertTrue(sellerSeatIdOptional.isPresent());
    assertEquals(Long.valueOf(1), sellerSeatIdOptional.get());
  }

  @Test
  void shouldNotReturnSellerSeatIdForExistingCompanyThatHasThisColumnNull() {
    // given
    Long companyPid = 400L;

    // when
    Optional<Long> sellerSeatIdOptional = companyRepository.findSellerSeatIdByPid(companyPid);

    // then
    assertFalse(sellerSeatIdOptional.isPresent());
  }

  @Test
  void shouldNotReturnSellerSeatIdForNotExistingCompany() {
    // given
    Long companyPid = -1L;

    // when
    Optional<Long> sellerSeatIdOptional = companyRepository.findSellerSeatIdByPid(companyPid);

    // then
    assertFalse(sellerSeatIdOptional.isPresent());
  }

  @Test
  void shouldReturnIsNotDefaultRTBProfilesEnabled() {
    // given
    Long companyPid = 2L;

    // when
    Boolean defaultRTBProfilesEnabled =
        companyRepository.isCompanyDefaultRTBProfilesEnabled(companyPid);

    // then
    assertFalse(defaultRTBProfilesEnabled);
  }

  @Test
  void shouldReturnIstDefaultRTBProfilesEnabled() {
    // given
    Long companyPid = 4L;

    // when
    Boolean defaultRTBProfilesEnabled =
        companyRepository.isCompanyDefaultRTBProfilesEnabled(companyPid);

    // then
    assertTrue(defaultRTBProfilesEnabled);
  }

  @Test
  void shouldReturnMdmIdsIfSetOnActiveCompany() {
    // given
    Long companyPid = 5L;
    Company company = companyRepository.getOne(companyPid);

    // when
    List<MdmId> mdmIds = companyRepository.findMdmIdsByActiveCompanyPid(companyPid);

    // then
    CompanyMdmId mdmId1 = new CompanyMdmId();
    mdmId1.setPid(1L);
    mdmId1.setId("mdm1");
    mdmId1.setLastUpdate(new Date(1625134210000L));
    mdmId1.setCompany(company);
    CompanyMdmId mdmId2 = new CompanyMdmId();
    mdmId2.setPid(2L);
    mdmId2.setId("mdm2");
    mdmId2.setLastUpdate(new Date(1625137871000L));
    mdmId2.setCompany(company);

    assertEquals(List.of(mdmId1, mdmId2), mdmIds);
  }

  @Test
  void shouldReturnEmptyValuesIfMdmIdsSetOnInactiveCompany() {
    // given
    Long companyPid = 4L;

    // when
    List<MdmId> mdmIds = companyRepository.findMdmIdsByActiveCompanyPid(companyPid);

    // then
    assertTrue(mdmIds.isEmpty());
  }

  @Test
  void shouldReturnEmptyValueIfMdmIdsNotSetOnCompany() {
    // given
    Long companyPid = 3L;

    // when
    List<MdmId> mdmIds = companyRepository.findMdmIdsByActiveCompanyPid(companyPid);

    // then
    assertTrue(mdmIds.isEmpty());
  }

  @Test
  void shouldReturnCompanyWithGivenType() {
    // when
    List<Company> companies =
        companyRepository.findAll(CompanySpecification.withType(CompanyType.NEXAGE));

    // then
    assertEquals(1, companies.size());
    assertEquals(1L, companies.get(0).getPid());
  }

  @Test
  void shouldReturnCompanyWithGivenStatus() {
    // when
    List<Company> companies =
        companyRepository.findAll(CompanySpecification.withStatus(Status.ACTIVE));

    // then
    assertEquals(2, companies.size());
    assertEquals(5L, companies.get(0).getPid());
    assertEquals(10L, companies.get(1).getPid());
  }

  @Test
  void shouldReturnCompanyWithDefaultRtbProfilesEnabled() {
    // when
    List<Company> companies =
        companyRepository.findAll(CompanySpecification.withDefaultRtbProfilesEnabled(true));

    // then
    assertEquals(3, companies.size());
    assertEquals(
        Set.of(4L, 5L, 10L), companies.stream().map(BaseModel::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnCompanyWithRtbEnabled() {
    // when
    List<Company> companies = companyRepository.findAll(CompanySpecification.withRtbEnabled(true));

    // then
    assertEquals(1, companies.size());
    assertEquals(2L, companies.get(0).getPid());
  }

  @Test
  void shouldReturnSellerCompaniesWithNameLikeGivenTermAndRtbDisabled() {
    // when
    List<Company> companies =
        companyRepository.findAll(
            CompanySpecification.ofSellerTypeWith(Set.of("name"), "Test", false));

    // then
    assertEquals(1, companies.size());
    assertEquals(4L, companies.get(0).getPid());
  }

  @Test
  void shouldReturnNoSellerCompaniesWithNameLikeGivenTermAndRtbEnabled() {
    // when
    List<Company> companies =
        companyRepository.findAll(
            CompanySpecification.ofSellerTypeWith(Set.of("name"), "Test", true));

    // then
    assertTrue(companies.isEmpty());
  }

  @Test
  void shouldReturnBuyerCompanyWithUrlLikeGivenTerm() {
    // when
    List<Company> companies =
        companyRepository.findAll(
            CompanySpecification.ofBuyerTypeWith(Set.of("website"), "buyer2", false));

    // then
    assertEquals(1, companies.size());
    assertEquals(8L, companies.get(0).getPid());
  }

  @Test
  void shouldReturnRtbEnabledSellers() {
    // when
    List<Company> companies = companyRepository.findAll(CompanySpecification.ofRtbEnabledSellers());

    // then
    assertEquals(1, companies.size());
    assertEquals(2L, companies.get(0).getPid());
  }

  private CompanyMetricsAggregation getCompanyMetricsAggregationForPublisher1(
      List<CompanyMetricsAggregation> content) {
    return content.stream()
        .filter(c -> c.getName().equals("Publisher 1"))
        .findFirst()
        .orElseThrow(RuntimeException::new);
  }

  private Date getDate(String date) {
    try {
      return dateFormat.parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldFindAllSellerMetaDataForCompanyProjections() {
    // when
    List<SellerMetaDataForCompanyProjection> sellerMetadataProjectionsList =
        companyRepository.findAllSellerMetaDataForCompanyProjections();

    // then
    assertEquals(1, sellerMetadataProjectionsList.size());
    assertEquals(6, sellerMetadataProjectionsList.get(0).getCompany());
    assertEquals(2, sellerMetadataProjectionsList.get(0).getSites());
    assertEquals(1, sellerMetadataProjectionsList.get(0).getTags());
    assertEquals(2, sellerMetadataProjectionsList.get(0).getHbsites());
    assertEquals(2, sellerMetadataProjectionsList.get(0).getUsers());
  }

  @Test
  void shouldFindSellerMetaDataForCompanyProjectionsByCompanyPid() {
    // given
    Long companyPid = 6L;

    // when
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjection =
        companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(companyPid);

    // then
    assertEquals(1, sellerMetaDataForCompanyProjection.size());
    assertEquals(6, sellerMetaDataForCompanyProjection.get(0).getCompany());
    assertEquals(2, sellerMetaDataForCompanyProjection.get(0).getSites());
    assertEquals(1, sellerMetaDataForCompanyProjection.get(0).getTags());
    assertEquals(2, sellerMetaDataForCompanyProjection.get(0).getHbsites());
    assertEquals(2, sellerMetaDataForCompanyProjection.get(0).getUsers());
  }

  @Test
  void shouldFindAllSeatHolderMetaDataReturnProjections() {
    // given / when
    List<AllSeatHolderMetaDataReturnProjection> allSeatHolderMetaDataReturnProjection =
        companyRepository.findAllSeatHolderMetaDataReturnProjections();

    // then
    assertEquals(1, allSeatHolderMetaDataReturnProjection.size());
    assertEquals(6, allSeatHolderMetaDataReturnProjection.get(0).getCompany());
  }

  @Test
  void shouldFindAllBuyerMetaDataForCompanyProjections() {
    // given / when
    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections =
        companyRepository.findAllBuyerMetaDataForCompanyProjections();

    // then
    assertEquals(3, buyerMetaDataForCompanyProjections.size());
    assertEquals(
        "D",
        buyerMetaDataForCompanyProjections.stream()
            .filter(p -> p.getCompany() == 5L)
            .findAny()
            .get()
            .getAdsource());
  }

  @Test
  void shouldFindBuyerMetaDataForCompanyProjectionsForCompanyPid() {
    // given
    Long companyPid = 5L;

    // when
    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections =
        companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(companyPid);

    // then
    assertEquals(1, buyerMetaDataForCompanyProjections.size());
  }

  @Test
  void shouldFindSearchSummaryProjectionsContaining() {
    // given
    String text = "text";

    // when
    List<SearchSummaryProjection> searchSummaryProjections =
        companyRepository.findSearchSummaryProjectionsContaining(text);

    // then
    assertEquals(2, searchSummaryProjections.size());
    SearchSummaryProjection searchSummaryProjection = searchSummaryProjections.get(0);
    assertEquals(3, searchSummaryProjection.getSitePid());
    assertEquals("Site with text-3", searchSummaryProjection.getSiteName());
    assertEquals(1, searchSummaryProjection.getSiteStatus());
    assertEquals(6, searchSummaryProjection.getCompanyPid());
    assertEquals("Nexage Inc 6", searchSummaryProjection.getCompanyName());
  }
}
