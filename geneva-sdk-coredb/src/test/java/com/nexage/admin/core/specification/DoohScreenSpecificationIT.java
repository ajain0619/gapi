package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.repository.DoohScreenRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/dooh-screen.sql", config = @SqlConfig(encoding = "utf-8"))
class DoohScreenSpecificationIT extends CoreDbSdkIntegrationTestBase {
  private static final long SELLER_PID = 812L;
  private static final long INVALID_SELLER_PID = 1L;
  @Autowired private DoohScreenRepository doohScreenRepository;

  @Test
  void shouldReturnSpecificationWithPid() {
    // given
    Specification<DoohScreen> spec = DoohScreenSpecification.withSellerPid(SELLER_PID);

    // when
    List<DoohScreen> result = doohScreenRepository.findAll(spec);

    // then
    assertAll(
        () -> assertEquals(2, result.size()),
        () -> assertEquals(1, result.get(0).getPid()),
        () -> assertEquals(SELLER_PID, result.get(0).getSellerPid()),
        () -> assertEquals("812-abcdefg", result.get(0).getSellerScreenId()),
        () -> assertEquals(2, result.get(1).getPid()),
        () -> assertEquals(SELLER_PID, result.get(1).getSellerPid()),
        () -> assertEquals("812-abcdefgh", result.get(1).getSellerScreenId()));
  }

  @Test
  void shouldReturnEmptyWhenSpecificationWithPidIsForInvalidSeller() {
    // given
    Specification<DoohScreen> spec = DoohScreenSpecification.withSellerPid(INVALID_SELLER_PID);
    // when
    List<DoohScreen> result = doohScreenRepository.findAll(spec);
    // then
    assertAll(() -> assertTrue(result.isEmpty()), () -> assertEquals(0, result.size()));
  }
}
