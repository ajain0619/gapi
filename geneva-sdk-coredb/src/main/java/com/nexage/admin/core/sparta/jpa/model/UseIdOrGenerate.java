package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

public class UseIdOrGenerate extends IdentityGenerator {

  public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
    if (obj == null) throw new HibernateException(new NullPointerException());

    Serializable id = null;
    if (obj instanceof Site) {
      id = ((Site) obj).getPid();
    } else if (obj instanceof Tag) {
      id = ((Tag) obj).getPid();
    } else if (obj instanceof AdSource) {
      id = ((AdSource) obj).getPid();
    }

    if (id == null) {
      id = super.generate(session, obj);
    }

    return id;
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    Serializable id =
        session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
    return id != null ? id : super.generate(session, object);
  }
}
