package com.nexage.app.web.report.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class BaseReportRequestIT extends BaseControllerItTest {
  @Mock protected UserContext userContext;

  @Test
  void shouldReturnCompanyPidsForUserContextCompanyPid() {
    // given
    when(userContext.getCompanyPids()).thenReturn(new HashSet<>(Collections.singletonList(1L)));

    // when
    Long companyPid = new BaseReportRequest.ReportUser(userContext).getCompany();

    // then
    assertEquals(1L, companyPid);
  }

  @Test
  void shouldReturnCompaniesPidForUserContextCompanies() {
    // given
    when(userContext.getCompanyPids()).thenReturn(new HashSet<>(Collections.singletonList(1L)));

    // when
    Set<Long> expectedCompaniesPids = new BaseReportRequest.ReportUser(userContext).getCompanies();

    // then
    assertEquals(1, expectedCompaniesPids.size());
  }

  @Test
  void shouldReturnUserNameForUserContextUserName() {
    // given
    when(userContext.getUserId()).thenReturn("testUser1");

    // when
    String expectedUserName = new BaseReportRequest.ReportUser(userContext).getUserName();

    // then
    assertNotNull(expectedUserName);
    assertEquals("testUser1", expectedUserName);
  }

  @Test
  void shouldReturnPidForUserContextUserPid() {
    // given
    when(userContext.getPid()).thenReturn(5L);

    // when
    Long expectedUserPid = new BaseReportRequest.ReportUser(userContext).getUserPid();

    // then
    assertNotNull(expectedUserPid);
    assertEquals(5L, expectedUserPid);
  }

  @Test
  void shouldBeNexageUserForUserContextUser() {
    // given
    when(userContext.isNexageUser()).thenReturn(true);

    // when
    boolean isNexageUser = new BaseReportRequest.ReportUser(userContext).isNexageUser();

    // then
    assertTrue(isNexageUser);
  }
}
