package com.nexage.admin.core.util;

import com.nexage.admin.core.model.Audit.AuditEntity;
import com.nexage.admin.core.model.Audit.AuditProduct;
import com.nexage.admin.core.model.Audit.AuditProperty;
import java.util.Map;

public interface Auditable {

  public AuditProduct getAuditProduct();

  public AuditEntity getAuditEntity();

  public Long getPid();

  public Map<AuditProperty, String> getAuditableData();
}
