package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.admin.core.specification.GeneralSpecification;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/iso-language-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class IsoLanguageRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private IsoLanguageRepository isoLanguageRepository;

  @Test
  void shouldFindAllLanguagesWhenSearchCriteriaIsNotSet() {
    List<IsoLanguage> isoLanguageList = isoLanguageRepository.findAll();
    assertEquals(5, isoLanguageList.size());
    assertEquals("Abkhazian", isoLanguageList.get(0).getLanguageName());
    assertEquals("ab", isoLanguageList.get(0).getLanguageCode());
    assertEquals(1, isoLanguageList.get(0).getPid());
    assertEquals("Greek", isoLanguageList.get(4).getLanguageName());
    assertEquals("el", isoLanguageList.get(4).getLanguageCode());
    assertEquals(5, isoLanguageList.get(4).getPid());
  }

  @Test
  void shouldFindMatchingLanguagesWhenSearchCriteriaIsSet() {
    Page<IsoLanguage> page =
        isoLanguageRepository.findAll(
            GeneralSpecification.withSearchCriteria(Set.of("languageName"), "Eng"),
            PageRequest.of(0, 10));
    assertEquals(1, page.getTotalElements());
    assertEquals("English", page.getContent().get(0).getLanguageName());
    assertEquals("en", page.getContent().get(0).getLanguageCode());
    assertEquals(3, page.getContent().get(0).getPid());
  }

  @Test
  void shouldFindMatchingLanguagesWhenFindByLanguageCodeIn() {
    List<IsoLanguage> data = isoLanguageRepository.findByLanguageCodeIn(Set.of("en", "el"));
    assertEquals(2, data.size());
    assertEquals("en", data.get(0).getLanguageCode());
    assertEquals("el", data.get(1).getLanguageCode());
  }

  @Test
  void shouldRejectNonMatchingLanguagesWhenFindByLanguageCodeIn() {
    List<IsoLanguage> data = isoLanguageRepository.findByLanguageCodeIn(Set.of("xx", "zz"));
    assertEquals(0, data.size());
  }
}
