package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.specification.CompanySpecification;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/company-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class CompanySpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private CompanyRepository companyRepository;
  private Specification<Company> companySpecification;

  private static final Status ACTIVE = Status.ACTIVE;
  private static final CompanyType BUYER = CompanyType.BUYER;
  private static final Set<String> NAME = Set.of("name");
  private static final String NEXAGE_INC_6 = "Nexage Inc 6";
  private static final String PUBLISHER_1 = "Publisher 1";
  private static final String SEATHOLDER_1 = "Seatholder_1";
  private static final CompanyType SELLER = CompanyType.SELLER;
  private static final String SELLER_1 = "Seller 1";
  private static final String TEST_4 = "Test 4";
  private static final String TEST_5 = "Test 5";
  private static final String QUERYTERM = "Test";

  @Test
  void shouldMakeRepositoryFindAllCompaniesWithNameLike() {
    // given
    companySpecification = CompanySpecification.withNameLike(TEST_5);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertFindCompanies(5L, TEST_5, BUYER, result);
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesWithType() {
    // given
    companySpecification = CompanySpecification.withType(SELLER);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(
        Set.of(2L, 3L, 4L, 6L), result.stream().map(Company::getPid).collect(Collectors.toSet()));
    assertEquals(
        Set.of(PUBLISHER_1, SELLER_1, TEST_4, NEXAGE_INC_6),
        result.stream().map(Company::getName).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesWithActiveStatus() {
    // given
    companySpecification = CompanySpecification.withStatus(ACTIVE);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(2, result.size());
    assertEquals(Set.of(5L, 10L), result.stream().map(Company::getPid).collect(Collectors.toSet()));
    assertEquals(
        Set.of(SEATHOLDER_1, TEST_5),
        result.stream().map(Company::getName).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesWithDefaultRtbProfilesEnabled() {
    // given
    companySpecification = CompanySpecification.withDefaultRtbProfilesEnabled(true);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(4L, 5L, 10L), result.stream().map(Company::getPid).collect(Collectors.toSet()));
    assertEquals(
        Set.of(TEST_4, TEST_5, SEATHOLDER_1),
        result.stream().map(Company::getName).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesWithRtbProfilesEnabled() {
    // given
    companySpecification = CompanySpecification.withRtbEnabled(true);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(1, result.size());
    assertFindCompanies(2L, PUBLISHER_1, SELLER, result);
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesOfSellerTypeWith() {
    // given
    companySpecification = CompanySpecification.ofSellerTypeWith(NAME, QUERYTERM, false);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(1, result.size());
    assertFindCompanies(4L, TEST_4, SELLER, result);
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesOfBuyerTypeWith() {
    // given
    companySpecification = CompanySpecification.ofBuyerTypeWith(NAME, QUERYTERM, false);

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(1, result.size());
    assertFindCompanies(5L, TEST_5, BUYER, result);
  }

  @Test
  void shouldMakeRepositoryFindAllCompaniesOfRtbEnabledSellers() {
    // given
    companySpecification = CompanySpecification.ofRtbEnabledSellers();

    // when
    List<Company> result = companyRepository.findAll(companySpecification);

    // then
    assertEquals(1, result.size());
    assertFindCompanies(2L, PUBLISHER_1, SELLER, result);
  }

  private void assertFindCompanies(
      Long pid, String companyName, CompanyType type, List<Company> result) {
    assertEquals(1, result.size());
    assertEquals(pid, result.get(0).getPid());
    assertEquals(companyName, result.get(0).getName());
    assertEquals(type, result.get(0).getType());
  }
}
