package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.ContentRating;
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
    scripts = "/data/repository/content-rating-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class ContentRatingRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private ContentRatingRepository contentRatingRepository;

  @Test
  void shouldFindAllRating() {
    Page<ContentRating> page =
        contentRatingRepository.findAll(
            GeneralSpecification.withSearchCriteria(Set.of("rating"), "TV"), PageRequest.of(0, 10));
    assertEquals(6, page.getTotalElements());
    assertEquals("TV-G", page.getContent().get(0).getRating());
    assertEquals(7, page.getContent().get(0).getPid());
  }

  @Test
  void shouldFindMatchingRating() {
    List<ContentRating> data = contentRatingRepository.findByRatingIn(Set.of("NR", "G"));
    assertEquals(2, data.size());
    assertEquals("NR", data.get(0).getRating());
    assertEquals("G", data.get(1).getRating());
  }
}
