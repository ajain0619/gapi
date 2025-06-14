package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.Site;
import java.io.Serializable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class SiteInterceptor extends EmptyInterceptor {

  /** */
  private static final long serialVersionUID = 2422901893754198981L;

  @Override
  public boolean onFlushDirty(
      Object entity,
      Serializable id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types) {
    boolean updated = false;
    if (entity instanceof Site) {
      for (int i = 0; i < propertyNames.length; i++) {
        if ("creationDate".equals(propertyNames[i])
            || "revenueLaunchDate".equals(propertyNames[i])) {
          if (null != previousState[i]) {
            currentState[i] = previousState[i];
            updated = true;
          }
        }
      }
    }
    return updated;
  }
}
