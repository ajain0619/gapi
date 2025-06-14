package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/post-auction-discount-type-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class PostAuctionDiscountTypeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PostAuctionDiscountTypeRepository repository;

  @Test
  void shouldCountByPidIn() {
    assertEquals(1L, repository.countByPidIn(List.of(1L, 3L)));
  }

  @Test
  void shouldFindByName() {
    List<PostAuctionDiscountType> result = repository.findByName("pad v2");

    assertEquals(1, result.size());
    assertEquals(2L, result.get(0).getPid());
  }
}
