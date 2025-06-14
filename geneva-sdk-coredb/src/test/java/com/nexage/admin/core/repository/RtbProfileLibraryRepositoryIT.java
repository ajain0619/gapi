package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/rtb-profile-library-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class RTBProfileLibraryRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired RTBProfileLibraryRepository rtbProfileLibraryRepository;

  @Test
  void shouldReturnRTBProfileLibraryWithExpectedPidAndPublisherPid() {
    // given
    Long libraryPid = 3L;
    Long publisherPid = 1L;

    // when
    Optional<RtbProfileLibrary> result =
        rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid);

    // then
    assertEquals(libraryPid, result.get().getPid());
    assertEquals(publisherPid, result.get().getPublisherPid());
  }

  @Test
  void shouldReturnRTBProfileLibrariesWithExpectedPrivilegeLevel() {
    // given
    RTBProfileLibraryPrivilegeLevel expectedPrivilegeLevel = RTBProfileLibraryPrivilegeLevel.GLOBAL;

    // when
    List<RtbProfileLibrary> result =
        rtbProfileLibraryRepository.findAllByPrivilegeLevel(expectedPrivilegeLevel);

    // then
    assertTrue(CollectionUtils.isNotEmpty(result));
    for (RtbProfileLibrary rtbProfileLibrary : result) {
      assertEquals(expectedPrivilegeLevel, rtbProfileLibrary.getPrivilegeLevel());
    }
  }

  @Test
  void shouldReturnRTBProfileLibrariesWithExpectedPublisherPid() {
    // given
    Long expectedPublisherPid = 1L;

    // when
    List<RtbProfileLibrary> result =
        rtbProfileLibraryRepository.findAllByPublisherPid(expectedPublisherPid);

    // then
    assertTrue(CollectionUtils.isNotEmpty(result));
    for (RtbProfileLibrary rtbProfileLibrary : result) {
      assertEquals(expectedPublisherPid, rtbProfileLibrary.getPublisherPid());
    }
  }

  @Test
  void shouldReturnRTBProfileLibrariesWithExpectedPublisherPidAndPrivilegeLevel() {
    // given
    Long expectedPublisherPid = 1L;
    RTBProfileLibraryPrivilegeLevel expectedPrivilegeLevel =
        RTBProfileLibraryPrivilegeLevel.PUBLISHER;

    // when
    List<RtbProfileLibrary> result =
        rtbProfileLibraryRepository.findAllByPublisherPidAndPrivilegeLevel(
            expectedPublisherPid, expectedPrivilegeLevel);

    // then
    assertTrue(CollectionUtils.isNotEmpty(result));
    for (RtbProfileLibrary rtbProfileLibrary : result) {
      assertEquals(expectedPublisherPid, rtbProfileLibrary.getPublisherPid());
      assertEquals(expectedPrivilegeLevel, rtbProfileLibrary.getPrivilegeLevel());
    }
  }
}
