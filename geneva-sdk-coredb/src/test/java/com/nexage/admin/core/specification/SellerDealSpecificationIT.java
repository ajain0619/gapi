package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDeal.DealStatus;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/specification/seller-deal-specification.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerDealSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final Long DEFAULT_PUBLISHER_PID = 1L;
  private static final Long DEFAULT_SITE_PID = 1L;
  private static final Long DEFAULT_POSITION_PID = 1L;
  private static final String DEFAULT_DEAL_PID = "1";

  @Autowired DirectDealRepository directDealRepository;
  @Autowired DealPublisherRepository dealPublisherRepository;
  @Autowired DealSiteRepository dealSiteRepository;
  @Autowired DealPositionRepository dealPositionRepository;
  @Autowired CompanyRepository companyRepository;
  @Autowired SiteRepository siteRepository;
  @Autowired PositionRepository positionRepository;

  private final PageRequest pageRequest =
      PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "description"));

  @Test
  void shouldDealIsAssociatedToPublisherThenDealIsReturned() throws SQLException {
    associateDealWithPublisher(DEFAULT_DEAL_PID, DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldDealIsAssociatedToPublisherBySiteThenDealIsReturned() {
    associateDealWithSite(DEFAULT_DEAL_PID, DEFAULT_SITE_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldDealIsAssociatedToPublisherByPositionThenDealIsReturned() {
    associateDealWithPosition(DEFAULT_DEAL_PID, DEFAULT_POSITION_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldDealsNotAssociatedWithSellerExistThenThoseDealsAreNotReturned() {
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(0, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldDealAssociatedWithOtherSellerByPositionExistsThenDealIsNotReturned() {

    associateDealWithPosition(DEFAULT_DEAL_PID, 2L);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(0, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealIdFilterArgumentIsPassedThenReturnedDealsAreFiltered() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, createQf("dealId"), "1", false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealIdMapFilterArgumentIsPassedThenReturnedDealsAreFiltered() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, Map.of("dealId", List.of("1")), false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDescriptionFilterArgumentIsPassedThenReturnedDealsAreFiltered() {

    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, createQf("description"), "Deal", false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealDescMapFilterArgumentIsPassedThenReturnedDealsAreFiltered() {

    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, Map.of("description", List.of("1234")), false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealCategoryFilterArgumentIsPassedThenReturnedDealsAreFiltered() {

    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, Map.of("dealCategory", List.of("SSP")), false),
            pageRequest);
    assertEquals(3, deals.getContent().size(), " number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealCategoriesFilterArgumentIsPassedThenReturnedDealsAreFiltered() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, Map.of("dealCategory", List.of("SSP")), false),
            pageRequest);
    assertEquals(3, deals.getContent().size(), " number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealCategoryAndDealDescFilterArgumentIsPassedThenReturnedDealsAreFiltered() {

    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID,
                Map.of("dealCategory", List.of("SSP"), "description", List.of("Deal 1")),
                false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), " number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andDealCategoryAndDealIdFilterArgumentIsPassedThenReturnedDealsAreFiltered() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID,
                Map.of("dealCategory", List.of("SSP"), "dealId", List.of("1")),
                false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), " number of deals returned");
  }

  @Test
  void shouldReturnDealAssociatedWithSellerBasedOnDealCategoryAndFewCharsFromDesc() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID,
                Map.of("dealCategory", List.of("SSP"), "description", List.of("12")),
                false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), " number of deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andBothFilterArgumentsArePassedThenReturnedDealsAreFiltered() {
    buildInsertDealStatement("2", "1234", true, 1);
    buildInsertDealStatement("3", "5678", true, 1);

    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, createQf("description", "dealId"), "3", false),
            pageRequest);
    assertEquals(2, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void whenDealsAssociatedWithSellerExist_andPagingArgumentsArePassedThenReturnedDealsArePaged() {

    buildInsertDealStatement("2", "Deal 2", true, 1);
    buildInsertDealStatement("3", "Deal 3", true, 1);
    buildInsertDealStatement("4", "Deal 4", true, 1);
    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("4", DEFAULT_PUBLISHER_PID);

    Page<DirectDeal> dealsPage1 =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            PageRequest.of(0, 2, Sort.by(Sort.Order.asc("description"))));
    assertEquals(2, dealsPage1.getContent().size(), "Incorrect number of deals returned");
    assertEquals(
        "Deal 1", dealsPage1.getContent().get(0).getDescription(), "Incorrect deals returned");
    assertEquals(
        "Deal 2", dealsPage1.getContent().get(1).getDescription(), "Incorrect deals returned");

    Page<DirectDeal> dealsPage2 =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            PageRequest.of(1, 2, Sort.by(Sort.Order.asc("description"))));
    assertEquals(2, dealsPage2.getContent().size(), "Incorrect number of deals returned");
    assertEquals(
        "Deal 3", dealsPage2.getContent().get(0).getDescription(), "Incorrect deals returned");
    assertEquals(
        "Deal 4", dealsPage2.getContent().get(1).getDescription(), "Incorrect deals returned");
  }

  @Test
  void
      whenDealsAssociatedWithSellerExist_andSingleSortArgumentIsPassedThenReturnedDealsAreSorted() {
    buildInsertDealStatement("2", "Deal 9", true, 1);
    buildInsertDealStatement("3", "Deal 8", true, 1);
    buildInsertDealStatement("4", "Deal 7", true, 1);

    associateDealWithPublisher("1", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("2", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("3", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("4", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(4, deals.getContent().size(), "Incorrect number of deals returned");
    assertEquals("Deal 1", deals.getContent().get(0).getDescription(), "Incorrect deals returned");
    assertEquals("Deal 7", deals.getContent().get(1).getDescription(), "Incorrect deals returned");
    assertEquals("Deal 8", deals.getContent().get(2).getDescription(), "Incorrect deals returned");
    assertEquals("Deal 9", deals.getContent().get(3).getDescription(), "Incorrect deals returned");
  }

  @Test
  void shouldSearchingBySellerAndDealPid_CorrectDealIsReturned() {
    // given
    String dealId = "10L";

    buildInsertDealStatement(dealId, "Deal 10", true, 1);
    DirectDeal directDeal = directDealRepository.findByDealId(dealId).get();
    associateDealWithPublisher(dealId, DEFAULT_PUBLISHER_PID);

    // when
    Optional<DirectDeal> deal =
        directDealRepository.findOne(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, directDeal.getPid(), false));

    // then
    assertTrue(deal.isPresent());
    assertEquals(dealId, deal.get().getDealId());
  }

  @Test
  void shouldReturnDealWithTrueVisibilityForMultiPartyDealCategory() {

    buildInsertDealStatement("11L", "Deal 10", false, 2);
    buildInsertDealStatement("12L", "Deal 12", true, 2);
    associateDealWithPublisher("11L", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("12L", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(DEFAULT_PUBLISHER_PID, null, null, false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldReturnDealWithTrueVisibilityWhileSearchingADealParamMap() {

    buildInsertDealStatement("10", "Deal 10", false, 2);
    buildInsertDealStatement("12", "Deal 12", true, 2);
    associateDealWithPublisher("10", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("12", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, Map.of("description", List.of("Deal")), true),
            pageRequest);
    assertEquals(2, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldReturnDealWithTrueVisibilityWhileSearchingADeal() {

    buildInsertDealStatement("10", "Deal 10", false, 2);
    buildInsertDealStatement("12", "Deal 12", true, 2);
    associateDealWithPublisher("10", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("12", DEFAULT_PUBLISHER_PID);
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, createQf("description"), "Deal", true),
            pageRequest);
    assertEquals(2, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldReturnDealWithTrueVisibilityForMultiPartyDealCategoryBasedOnDealPid() {

    buildInsertDealStatement("10", "Deal 10", false, 2);
    buildInsertDealStatement("12", "Deal 12", true, 2);
    associateDealWithPublisher("10", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("12", DEFAULT_PUBLISHER_PID);
    DirectDeal directDeal = directDealRepository.findByDealId("10").get();
    Page<DirectDeal> deal =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, directDeal.getPid(), false),
            pageRequest);
    assertEquals(0, deal.getContent().size(), "Incorrect number of deals returned");
    directDeal = directDealRepository.findByDealId("12").get();
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, directDeal.getPid(), false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  @Test
  void shouldReturnDealWithTrueVisibilityForAnyDealCategory() throws SQLException {

    buildInsertDealStatement("10", "Deal 10", false, 3);
    buildInsertDealStatement("12", "Deal 12", true, 3);
    associateDealWithPublisher("10", DEFAULT_PUBLISHER_PID);
    associateDealWithPublisher("12", DEFAULT_PUBLISHER_PID);
    DirectDeal directDeal = directDealRepository.findByDealId("10").get();
    Page<DirectDeal> deal =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, directDeal.getPid(), false),
            pageRequest);
    assertEquals(0, deal.getContent().size(), "Incorrect number of deals returned");
    directDeal = directDealRepository.findByDealId("12").get();
    Page<DirectDeal> deals =
        directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(
                DEFAULT_PUBLISHER_PID, directDeal.getPid(), false),
            pageRequest);
    assertEquals(1, deals.getContent().size(), "Incorrect number of deals returned");
  }

  private void buildInsertDealStatement(
      String id, String description, boolean visibility, int dealCategory) {
    DirectDeal directDeal = new DirectDeal();
    directDeal.setDealId(id);
    directDeal.setVersion(0);
    directDeal.setDescription(description);
    directDeal.setCurrency("EUR");
    directDeal.setPriorityType(DealPriorityType.OPEN);
    directDeal.setVisibility(visibility);
    directDeal.setDealCategory(dealCategory);
    directDeal.setStatus(DealStatus.Active);
    directDealRepository.saveAndFlush(directDeal);
  }

  private void buildInsertSiteStatement(String id, Long companyPid) {
    Site site = new Site();
    Company company = companyRepository.getOne(companyPid);
    site.setCompany(company);
    site.setCompanyPid(company.getPid());
    site.setId(id);
    site.setDcn("dcn-" + id);
    site.setType(Type.APPLICATION);
    site.setName("name-" + id);
    site.setPlatform(Platform.ANDROID);
    site.setDomain("https://www.hell.com");
    site.setUrl("https://www.hell.com");
    siteRepository.saveAndFlush(site);
  }

  private void buildInsertPositionStatement(String id, Long sitePid) {
    Position position = new Position();
    position.setSite(siteRepository.getOne(sitePid));
    position.setName("pos-" + id);
    position.setMraidAdvancedTracking(false);
    position.setScreenLocation(ScreenLocation.UNKNOWN);
    position.setVideoSupport(VideoSupport.BANNER);
    position.setVersion(0);
    position.setMraidSupport(MRAIDSupport.YES);
    positionRepository.saveAndFlush(position);
  }

  private void associateDealWithPublisher(String id, Long publisherPid) {
    DealPublisher dealPublisher = new DealPublisher();
    dealPublisher.setDeal(directDealRepository.findByDealId(id).get());
    dealPublisher.setPubPid(publisherPid);
    dealPublisher.setVersion(0);
    dealPublisherRepository.saveAndFlush(dealPublisher);
  }

  private void associateDealWithSite(String deaId, Long sitePid) {
    DealSite dealSite = new DealSite();
    dealSite.setDeal(directDealRepository.findByDealId(deaId).get());
    dealSite.setSitePid(sitePid);
    dealSite.setVersion(0);
    dealSiteRepository.saveAndFlush(dealSite);
  }

  private void associateDealWithPosition(String id, Long positionPid) {
    DealPosition dealPosition = new DealPosition();
    dealPosition.setDeal(directDealRepository.findByDealId(id).get());
    dealPosition.setPositionPid(positionPid);
    dealPosition.setVersion(0);
    dealPositionRepository.saveAndFlush(dealPosition);
  }

  private Set<String> createQf(String... fields) {
    return new HashSet<>(List.of(fields));
  }
}
