package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.dto.TagHierarchyDto;
import com.nexage.admin.core.enums.Status;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/tag-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class TagRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private TagRepository tagRepository;

  @Test
  void shouldFetchTagHierarchy() {
    // when
    Set<TagHierarchyDto> result = tagRepository.getTagHierarchy(1L, 1L, 1L);

    // then
    assertEquals(1, result.size());

    TagHierarchyDto dto = result.stream().findFirst().orElseThrow();
    assertEquals(1L, dto.getTagPid());
    assertEquals("sometag", dto.getTagName());
    assertEquals(1, dto.getTierLevel());
    assertEquals(1L, dto.getTierPid());
    assertEquals((short) 0, dto.getTierType());
    assertEquals(1, dto.getBelongsToRtbGroup());
    assertFalse(dto.getFilterBiddersAllowlist());
    assertFalse(dto.getFilterBiddersWhitelist());
    assertFalse(dto.getUseDefaultBlock());
    assertFalse(dto.getUseDefaultBidders());
  }

  @Test
  void shouldCountBySitePidAndPositionPidAndStatusCorrectly() {
    // when
    long tagCount = tagRepository.countBySitePidAndPositionPidAndStatusNot(1L, 1L, Status.DELETED);

    // then
    assertEquals(1L, tagCount);
  }
}
