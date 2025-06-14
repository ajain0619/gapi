package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuditTest {
  @Test
  void shouldReturnCorrectAuditPropertyForTargetType() {
    // given
    var targetType = Target.TargetType.ZONE;

    // when
    var auditProperty = Audit.AuditProperty.getAuditPropertyForTargetType(targetType);

    // then
    assertEquals(Audit.AuditProperty.DEPLOYMENT, auditProperty);
  }
}
