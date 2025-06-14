package com.nexage.admin.core.repository;

import static com.nexage.admin.core.util.TestUtil.TEST_PREFIX;
import static com.nexage.admin.core.util.TestUtil.getTestCreative;
import static com.nexage.admin.core.util.TestUtil.validateCreative;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Creative;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/creative-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class CreativeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final long CAMPAIGN_PID = 400L;
  private static final long NON_DELETED_CREATIVE_PID = 1L;

  @Autowired protected CompanyRepository companyRepository;

  @Autowired protected CreativeRepository creativeRepository;

  @Test
  void shouldCreateCreative() {
    Creative creative = getTestCreative();
    creative = creativeRepository.save(creative);
    validateCreative(creative, TEST_PREFIX);
  }

  @Test
  void shouldFindOneNonDeletedCreative() {
    // when
    List<Creative> creatives = creativeRepository.findAllNonDeletedByCampaignPid(CAMPAIGN_PID);

    // then
    assertEquals(1, creatives.size());
    assertEquals(NON_DELETED_CREATIVE_PID, creatives.get(0).getPid());
  }

  @Test
  void shouldCountNonDeletedCreativesCorrectly() {
    // when
    long creativeCount = creativeRepository.countAllNonDeletedByCampaignPid(CAMPAIGN_PID);

    // then
    assertEquals(1L, creativeCount);
  }
}
