package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql(
    scripts = "/data/repository/attributes-company-visibility-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class AttributesCompanyVisibilityRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private AttributeCompanyVisibilityRepository attributeCompanyVisibilityRepository;

  @Test
  void shouldFindCompaniesForAttributes() {
    // given
    List<Long> attrPids = new ArrayList<>();
    attrPids.add(1L);
    attrPids.add(3L);
    List<Long> pubPids = attributeCompanyVisibilityRepository.findCompaniesForAttributes(attrPids);
    assertEquals(2, pubPids.size());
    assertEquals(1L, (long) pubPids.get(0));
    assertEquals(3L, (long) pubPids.get(1));
    List<Long> newAttrPids = new ArrayList<>();
    newAttrPids.add(1L);
    newAttrPids.add(2L);
    newAttrPids.add(3L);
    newAttrPids.add(4L);

    // when
    List<Long> newPubPids =
        attributeCompanyVisibilityRepository.findCompaniesForAttributes(newAttrPids);

    // then
    assertEquals(4, newAttrPids.size());
    assertEquals(3, newPubPids.size());
    assertEquals(1L, (long) newPubPids.get(0));
    assertEquals(2L, (long) newPubPids.get(1));
    assertEquals(3L, (long) newPubPids.get(2));
  }
}
