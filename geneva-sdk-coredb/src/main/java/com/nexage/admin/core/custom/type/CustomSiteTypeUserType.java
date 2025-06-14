package com.nexage.admin.core.custom.type;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.nexage.admin.core.enums.site.PublisherSiteType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSiteTypeUserType implements UserType {

  private static final int[] SQL_TYPES = {Types.VARCHAR};

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final Splitter SPLIT_ON_COMMA = Splitter.on(",").omitEmptyStrings().trimResults();

  private static Joiner JOIN_WITH_COMMA = Joiner.on(",").skipNulls();

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class<PublisherSiteType> returnedClass() {
    return PublisherSiteType.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) {
      return true;
    }
    if (null == x || null == y) {
      return false;
    }
    return x.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    Set<PublisherSiteType> result = new HashSet<>();
    if (!rs.wasNull()) {
      String value = rs.getString(names[0]);
      for (Iterator<String> it = SPLIT_ON_COMMA.splitToList(value).iterator(); it.hasNext(); ) {
        result.add(PublisherSiteType.parse(it.next()));
      }
    }

    return result;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value != null) {
      String csvString = JOIN_WITH_COMMA.join((Iterable<?>) value);
      st.setString(index, csvString);
    } else {
      st.setString(index, null);
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}
