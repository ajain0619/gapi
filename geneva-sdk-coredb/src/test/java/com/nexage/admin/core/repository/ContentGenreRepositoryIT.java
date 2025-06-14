package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.ContentGenre;
import com.nexage.admin.core.specification.GeneralSpecification;
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
    scripts = "/data/repository/content-genre-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class ContentGenreRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private ContentGenreRepository contentGenreRepository;

  @Test
  void shouldFindAllGenre() {
    Page<ContentGenre> page =
        contentGenreRepository.findAll(
            GeneralSpecification.withSearchCriteria(Set.of("genre"), "Adv"), PageRequest.of(0, 10));
    assertEquals(1, page.getTotalElements());
    assertEquals("Adventure", page.getContent().get(0).getGenre());
    assertEquals(2, page.getContent().get(0).getPid());
  }

  @Test
  void shouldFindMatchingGenre() {
    int hasGenre = contentGenreRepository.existsByGenre(Set.of("Action", "Comedy"));
    assertEquals(2, hasGenre);
  }
}
