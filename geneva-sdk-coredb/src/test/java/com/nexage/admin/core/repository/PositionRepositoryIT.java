package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.specification.PositionSpecification;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/position-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PositionRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PositionRepository positionRepository;

  @Test
  void shouldFindByPidIn() {
    var out = positionRepository.findAllByPidIn(List.of(1L, 2L));
    assertEquals(2, out.size());
    out.forEach(pv -> assertEquals(1L, pv.getSitePid().longValue()));
  }

  @Test
  void shouldCheckIfVideoSupportByPidExists() {
    VideoSupport videoSupport = positionRepository.findVideoSupportByPlacementPid(1L);
    assertEquals(VideoSupport.VIDEO, videoSupport);
    videoSupport = positionRepository.findVideoSupportByPlacementPid(2L);
    assertEquals(VideoSupport.VIDEO_AND_BANNER, videoSupport);
  }

  @Test
  void shouldCheckIfPositiontByPidExists() {
    assertTrue(positionRepository.existsByPid(2L));
    assertFalse(positionRepository.existsByPid(5L));
  }

  @Test
  void shouldCountEffectivePositionCountForPositionRtbProfile() {
    long number = positionRepository.count(PositionSpecification.withDefaultRtbProfiles(60000L));
    assertEquals(2, number);
  }

  @Test
  void shouldCountEffectivePositionCountForSellerAttributeRtbProfile() {
    long number = positionRepository.count(PositionSpecification.withDefaultRtbProfiles(60001L));
    assertEquals(1, number);
  }

  @Test
  void shouldCountEffectivePositionCountForSiteRtbProfile() {
    long number = positionRepository.count(PositionSpecification.withDefaultRtbProfiles(60002L));
    assertEquals(1, number);
  }

  @Test
  void shouldCountBySitePidAndStatusCorrectly() {
    // when
    long positionCount = positionRepository.countBySitePidAndStatusNot(3L, Status.DELETED);

    // then
    assertEquals(1L, positionCount);
  }

  @Test
  void shouldGetAllWithMemo() {
    // given
    String memo = "test_memo";
    Specification<Position> spec = PositionSpecification.withMemo(memo);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(5, number);
  }

  @Test
  void shouldGetAllWithSellerId() {
    // given
    Long sellerId = 1L;
    Specification<Position> spec = PositionSpecification.withSellerId(sellerId);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(2, number);
  }

  @Test
  void shouldGetAllWithPositionTypes() {
    // given
    List<String> placementTypes = List.of("BANNER", "NATIVE");
    Specification<Position> spec = PositionSpecification.withPositionTypes(placementTypes);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(4, number);
  }

  @Test
  void shouldGetAllWithSiteId() {
    // given
    Long siteId = 1L;
    Specification<Position> spec = PositionSpecification.withSiteId(siteId);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(2, number);
  }

  @Test
  void shouldGetAllWithStatus() {
    // given
    List<String> status = List.of("ACTIVE");
    Specification<Position> spec = PositionSpecification.withStatus(status);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(4, number);
  }

  @Test
  void shouldGetAllWithExactName() {
    // given
    String name = "footer";
    Specification<Position> spec = PositionSpecification.withExactName(name);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(1, number);
  }

  @Test
  void shouldGetAllWithExactMemo() {
    // given
    String memo = "test_memo3";
    Specification<Position> spec = PositionSpecification.withExactMemo(memo);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(1, number);
  }

  @Test
  void shouldGetAllWithExactAlias() {
    // given
    String alias = "alias test3";
    Specification<Position> spec = PositionSpecification.withExactAlias(alias);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(1, number);
  }

  @Test
  void shouldGetAllWithNameAndMemoWhenNameIsPresent() {
    // given
    Map<String, String> terms = Map.ofEntries(Map.entry("name", "footer"));

    Specification<Position> spec = PositionSpecification.withNameAndMemo(terms);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(1, number);
  }

  @Test
  void shouldGetAllWithNameAndMemoWhenMemoIsPresent() {
    // given
    Map<String, String> terms = Map.ofEntries(Map.entry("memo", "deleted"));

    Specification<Position> spec = PositionSpecification.withNameAndMemo(terms);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(1, number);
  }

  @Test
  void shouldGetAllWithNameAndMemoWhenNameAndMemoArePresent() {
    // given
    Map<String, String> terms =
        Map.ofEntries(Map.entry("name", "header"), Map.entry("memo", "left"));

    Specification<Position> spec = PositionSpecification.withNameAndMemo(terms);

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(2, number);
  }

  @Test
  void shouldThrowExceptionWithQueryTermsWhenQueryIsNotPresent() {
    // given
    Map<String, String> terms = Map.ofEntries(Map.entry("name", "footer"));

    Optional<Specification<Position>> spec = PositionSpecification.withQueryTerms(terms);

    // when and then
    assertThrows(NoSuchElementException.class, () -> spec.get());
  }

  @Test
  void shouldGetAllWithQueryTermsWhenQueryIsSearch() {
    // given
    Map<String, String> terms =
        Map.ofEntries(
            Map.entry("action", "search"),
            Map.entry("name", "footer"),
            Map.entry("memo", "deleted"));

    Optional<Specification<Position>> optionalSpec = PositionSpecification.withQueryTerms(terms);

    Specification<Position> spec = optionalSpec.get();

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(2, number);
  }

  @Test
  void shouldGetAllWithQueryTermsWhenQueryIsDuplicate() {
    Map<String, String> terms =
        Map.ofEntries(
            Map.entry("action", "duplicate"),
            Map.entry("name", "footer"),
            Map.entry("memo", "test_memo3"),
            Map.entry("alias", "alias test3"));

    Optional<Specification<Position>> optionalSpec = PositionSpecification.withQueryTerms(terms);

    Specification<Position> spec = optionalSpec.get();

    // when
    long number = positionRepository.count(spec);

    // then
    assertEquals(3, number);
  }

  @Test
  void shouldThrowExceptionWithQueryTermsWhenQueryIsDefault() {
    // given
    Map<String, String> terms =
        Map.ofEntries(Map.entry("action", "default"), Map.entry("name", "footer"));

    Optional<Specification<Position>> spec = PositionSpecification.withQueryTerms(terms);

    // when and then
    assertThrows(NoSuchElementException.class, () -> spec.get());
  }
}
