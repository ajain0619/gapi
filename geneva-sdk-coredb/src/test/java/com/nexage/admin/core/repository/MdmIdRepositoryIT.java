package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.CompanyMdmView;
import com.nexage.admin.core.model.MdmId;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/mdmid-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class MdmIdRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired MdmIdRepository mdmIdRepository;

  @Test
  void shouldReturnCorrectCompanyAndSellerSeatMdmIds() {
    Page<CompanyMdmView> results =
        mdmIdRepository.findMdmIdsForCompaniesIn(Set.of(2L, 3L, 4L), Pageable.unpaged());

    assertEquals(3, results.getTotalElements());

    CompanyMdmView result0 = results.getContent().get(0);
    assertEquals(2L, result0.getPid());
    assertEquals(
        Set.of("mdm1", "mdm2"),
        result0.getMdmIds().stream().map(MdmId::getId).collect(Collectors.toSet()));
    assertEquals(
        Set.of("mdm3"),
        result0.getSellerSeat().getMdmIds().stream().map(MdmId::getId).collect(Collectors.toSet()));

    CompanyMdmView result1 = results.getContent().get(1);
    assertEquals(3L, result1.getPid());
    assertTrue(result1.getMdmIds().isEmpty());
    assertTrue(result1.getSellerSeat().getMdmIds().isEmpty());

    CompanyMdmView result2 = results.getContent().get(2);
    assertEquals(4L, result2.getPid());
    assertEquals(
        Set.of("mdm4"), result2.getMdmIds().stream().map(MdmId::getId).collect(Collectors.toSet()));
    assertNull(result2.getSellerSeat());
  }
}
