package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.CrsTagMapping;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/crs-tag-mapping-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class CrsTagMappingRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private CrsTagMappingRepository crsTagMappingRepository;
  @Autowired private BrandProtectionTagRepository brandProtectionTagRepository;

  @Test
  void shouldCreateCrsTagMapping() {
    // given
    Date date = new Date();
    long crsTagId = 2L;
    long crsTagAttributeId = 3L;
    BrandProtectionTag tag =
        brandProtectionTagRepository
            .findById(18L)
            .orElseThrow(() -> new EntityNotFoundException("Brand protection tag not found"));
    CrsTagMapping tagMapping = new CrsTagMapping();
    tagMapping.setTag(tag);
    tagMapping.setCrsTagId(crsTagId);
    tagMapping.setCrsTagAttributeId(crsTagAttributeId);
    tagMapping.setUpdateDate(date);

    // when
    CrsTagMapping saved = crsTagMappingRepository.save(tagMapping);

    // then
    assertNotNull(saved.getPid());
    assertEquals(date, saved.getUpdateDate());
    assertEquals(tag, saved.getTag());
    assertEquals(crsTagId, saved.getCrsTagId());
    assertEquals(crsTagAttributeId, saved.getCrsTagAttributeId());
  }

  @Test
  void shouldFindCrsTagMappingById() {
    // given
    long pid = 1L;

    // when
    CrsTagMapping tagMapping =
        crsTagMappingRepository
            .findById(pid)
            .orElseThrow(() -> new EntityNotFoundException("Crs Tag Mapping not found in DB"));

    // then
    assertEquals(pid, tagMapping.getPid());
  }

  @Test
  void shouldUpdateCrsTagMapping() {
    // given
    BrandProtectionTag tag =
        brandProtectionTagRepository
            .findById(20L)
            .orElseThrow(() -> new EntityNotFoundException("Brand Protection Tag not found in DB"));
    CrsTagMapping tagMapping =
        crsTagMappingRepository
            .findById(1L)
            .orElseThrow(() -> new EntityNotFoundException("Crs Tag Mapping not found in DB"));
    tagMapping.setCrsTagId(100L);
    tagMapping.setCrsTagAttributeId(101L);
    tagMapping.setTag(tag);

    // when
    CrsTagMapping updated = crsTagMappingRepository.save(tagMapping);

    // then
    assertEquals(tagMapping, updated);
  }

  @Test
  void shouldDeleteCrsTagMapping() {
    // given
    long pid = 1L;

    // when
    crsTagMappingRepository.deleteById(pid);

    // then
    assertTrue(crsTagMappingRepository.findById(pid).isEmpty());
  }

  @Test
  void shouldFindAllCrsTagMappings() {
    // when
    List<CrsTagMapping> crsTagMappings = crsTagMappingRepository.findAll();

    // then
    assertEquals(2, crsTagMappings.size());
  }
}
