package com.nexage.app.job;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class PlacementDTOFormulaAutoUpdateJobTest {
  @Mock UserDetailsService userDetailsService;
  @Mock RuleFormulaUpdateService ruleFormulaUpdateService;

  private PlacementFormulaAutoUpdateJob job;

  @BeforeEach
  void setUp() {
    job = new PlacementFormulaAutoUpdateJob(userDetailsService, ruleFormulaUpdateService, "admin");
  }

  @Test
  void shouldRuleFormulaTaskWillBeExecutedWhenJobRuns() {
    when(ruleFormulaUpdateService.findAllToUpdate()).thenReturn(Collections.singletonList(8L));

    SpringUserDetails userDetails = mock(SpringUserDetails.class);
    when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

    // method under test
    assertTrue(job.runJob());

    verify(ruleFormulaUpdateService, times(1)).findAllToUpdate();
    verify(ruleFormulaUpdateService, times(1)).tryUpdate(eq(8L), any());
  }

  @Test
  void shouldRuleFormulaUpdateServiceThrowsExceptionWhenJobRuns() {
    when(ruleFormulaUpdateService.findAllToUpdate()).thenThrow(new RuntimeException());

    SpringUserDetails userDetails = mock(SpringUserDetails.class);
    when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

    // method under test
    assertFalse(job.runJob());

    verify(ruleFormulaUpdateService, times(1)).findAllToUpdate();
    verify(ruleFormulaUpdateService, times(1)).getEntityType();
  }
}
