package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Audit;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
class AuditRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired AuditRepository auditRepository;

  @Test
  void shouldSaveNewAudit() {
    // given
    Date modifiedDate = new Date();
    Audit audit =
        new Audit(
            modifiedDate,
            1L,
            Audit.AuditProduct.ADSERVER,
            Audit.AuditEntity.CAMPAIGN,
            2L,
            Audit.AuditProperty.STATUS,
            null,
            "ACTIVE");

    // when
    audit = auditRepository.save(audit);

    // then
    assertNotNull(audit.getPid());
    assertEquals(Audit.AuditEntity.CAMPAIGN, audit.getEntity());
    assertEquals(2L, audit.getEntityPid());
    assertEquals(modifiedDate, audit.getModifiedDate());
    assertEquals("ACTIVE", audit.getNewValue());
    assertEquals(Audit.AuditProduct.ADSERVER, audit.getProduct());
    assertNull(audit.getPreviousValue());
    assertEquals(Audit.AuditProperty.STATUS, audit.getProperty());
    assertEquals(1L, audit.getUserPid());
  }
}
