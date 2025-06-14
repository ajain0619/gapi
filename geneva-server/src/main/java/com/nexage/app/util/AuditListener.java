package com.nexage.app.util;

import com.nexage.admin.core.model.Audit;
import com.nexage.admin.core.model.Audit.AuditProperty;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.AuditRepository;
import com.nexage.admin.core.util.Auditable;
import java.util.Date;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuditListener<T extends Auditable> {

  private final Long pid;

  private final Map<AuditProperty, String> originalAuditData;

  public AuditListener(T original) {
    pid = original.getPid();
    originalAuditData = original.getAuditableData();
  }

  public void notifyModification(User user, T modified, AuditRepository auditRepository) {

    if (!pid.equals(modified.getPid())) {
      if (log.isWarnEnabled()) {
        log.warn("audit listener was notified of an unknown entity modification: {}", modified);
      }
      return;
    }
    if (null == user) {
      if (log.isWarnEnabled()) {
        log.warn("audit listener was notified of a modification made by a null user");
      }
      return;
    }

    Date modifiedDate = new Date();
    Map<AuditProperty, String> modifiedAuditData = modified.getAuditableData();

    String originalValue;
    String newValue;
    Audit audit;
    for (AuditProperty property : originalAuditData.keySet()) {

      originalValue = originalAuditData.get(property);
      newValue = modifiedAuditData.get(property);

      if (originalValue != null || newValue != null) {
        if ((null == originalValue && newValue != null) || !(originalValue.equals(newValue))) {
          audit =
              new Audit(
                  modifiedDate,
                  user.getPid(),
                  modified.getAuditProduct(),
                  modified.getAuditEntity(),
                  modified.getPid(),
                  property,
                  originalValue,
                  newValue);
          auditRepository.save(audit);
        }
      }
    }
  }
}
