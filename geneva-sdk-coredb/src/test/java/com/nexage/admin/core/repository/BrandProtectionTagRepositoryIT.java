package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.specification.BrandProtectionTagSpecification;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/brand-protection-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BrandProtectionTagRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BrandProtectionTagRepository tagRepository;
  private static final long TAG_PID = 18L;

  @Test
  void shouldGetAllTagsAssignedToGivenCategory() {
    // given
    Set<Long> pids = ImmutableSet.of(18L, 19L, 20L);

    // when
    long count = tagRepository.countByCategoryPidAndPidIn(2L, pids);

    // then
    assertEquals(pids.size(), count);
  }

  @Test
  void shouldFindBrandProtectionTagByPid() {
    // when
    BrandProtectionTag tag =
        tagRepository
            .findById(TAG_PID)
            .orElseThrow(() -> new EntityNotFoundException("BrandProtectionTag not found"));

    // then
    assertEquals(TAG_PID, tag.getPid());
    assertEquals("ActiveX", tag.getName());
  }

  @Test
  void shouldCreateBrandProtectionTag() {
    // given
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setName("new-tag");
    tag.setRtbId("rtb-id");
    tag.setFreeTextTag(true);

    // when
    BrandProtectionTag result = tagRepository.save(tag);

    // then
    assertNotNull(result.getPid());
    assertEquals(tag, result);
  }

  @Test
  void shouldUpdateBrandProtectionTag() {
    // given
    String newName = "new-name";
    long newCategoryPid = 1L;
    BrandProtectionTag tag =
        tagRepository
            .findById(TAG_PID)
            .orElseThrow(() -> new EntityNotFoundException("BrandProtectionTag not found"));
    BrandProtectionCategory newCategory = new BrandProtectionCategory();
    newCategory.setPid(newCategoryPid);
    tag.setName(newName);
    tag.setCategory(newCategory);
    tag.setFreeTextTag(true);

    // when
    BrandProtectionTag updatedTag = tagRepository.save(tag);

    // then
    assertEquals(newName, updatedTag.getName());
    assertEquals(newCategoryPid, updatedTag.getCategory().getPid());
    assertTrue(updatedTag.getFreeTextTag());
  }

  @Test
  void shouldDeleteBrandProtectionTagByPid() {
    // when
    tagRepository.deleteById(TAG_PID);

    // then
    assertFalse(tagRepository.existsById(TAG_PID));
  }

  @Test
  void shouldFindAllBrandProtectionTags() {
    // when
    List<BrandProtectionTag> tags = tagRepository.findAll();

    // then
    assertEquals(11, tags.size());
  }

  @Test
  void shouldFindAllWithCategoryIdSpecification() {
    // given
    var spec = BrandProtectionTagSpecification.withCategoryId(2L);

    // when
    var result = tagRepository.findAll(spec);

    // then
    assertEquals(
        List.of("ActiveX", "Oath Banned Tracker", "Oath Unapproved Beacon"),
        result.stream().map(BrandProtectionTag::getName).collect(Collectors.toList()));
  }

  @Test
  void shouldFindAllWithQueryFieldsAndSearchTerm() {
    // given
    var spec =
        BrandProtectionTagSpecification.withQueryFieldsAndSearchTerm(Set.of("name"), "Active");

    // when
    var result = tagRepository.findAll(spec);

    // then
    assertEquals(
        List.of("ActiveX"),
        result.stream().map(BrandProtectionTag::getName).collect(Collectors.toList()));
  }

  @Test
  void shouldFindAllWithParentTagPid() {
    // given
    var spec = BrandProtectionTagSpecification.withParentTagPid(1L);

    // when
    var result = tagRepository.findAll(spec);

    // then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindAllWithNullParentTagPid() {
    // given
    var spec = BrandProtectionTagSpecification.withNullParentTagPid();

    // when
    var result = tagRepository.findAll(spec);

    // then
    assertEquals(
        List.of("ActiveX", "Oath Banned Tracker", "Oath Unapproved Beacon", "Business", "Science"),
        result.stream().map(BrandProtectionTag::getName).collect(Collectors.toList()));
  }
}
