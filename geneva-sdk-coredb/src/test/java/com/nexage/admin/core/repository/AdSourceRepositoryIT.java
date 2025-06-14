package com.nexage.admin.core.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.SelfServeEnablement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/adsource-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class AdSourceRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private AdSourceRepository adSourceRepository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  void shouldFindOneAdSourceByCompanyPid() {
    long companyPid = 1;
    long pid = 1;

    List<AdSource> adSourceList = adSourceRepository.findNonDeletedByCompanyPid(companyPid);

    assertThat(adSourceList, hasSize(1));
    assertThat(adSourceList.get(0).getPid(), is(equalTo(pid)));
  }

  @Test
  void shouldFindOneAdSourceBySelfServeEnablement() {
    AdSource.SelfServeEnablement selfServeEnablement = SelfServeEnablement.PUBLISHER;
    long pid = 2;

    List<AdSource> adSourceList =
        adSourceRepository.findNonDeletedBySelfServeEnablement(selfServeEnablement);

    assertThat(adSourceList, hasSize(1));
    assertThat(adSourceList.get(0).getPid(), is(equalTo(pid)));
  }

  @Test
  void shouldGetTwoAdSourcesFromFindAll() {
    List<AdSource> adSourceList = adSourceRepository.findAllNonDeleted();

    assertThat(adSourceList, hasSize(2));
  }

  @Test
  void shouldGetTwoAdSourcesFromFindByPidIn() {
    Set<Long> pids = Set.of(1L, 2L, 3L, 4L);

    List<AdSource> adSourceList = adSourceRepository.findNonDeletedByPidIn(pids);

    assertThat(adSourceList, hasSize(2));
  }

  @Test
  void shouldSetUpdateDateOnUpdate() {
    long pid = 1;
    Date date = Calendar.getInstance().getTime();
    AdSource adSource = adSourceRepository.findById(pid).orElseThrow(IllegalStateException::new);

    adSource.setName("Some other name");
    adSource = adSourceRepository.save(adSource);
    entityManager.flush();

    assertThat(adSource.getLastUpdate(), is(not(nullValue())));
    assertFalse(adSource.getLastUpdate().before(date));
  }
}
