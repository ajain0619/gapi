package com.nexage.admin.core.custom.type;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUserType implements UserType {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final Splitter SPLIT_ON_COMMA = Splitter.on(",").omitEmptyStrings().trimResults();

  private static Joiner JOIN_WITH_COMMA = Joiner.on(",").skipNulls();

  @Override
  public int[] sqlTypes() {
    return new int[] {java.sql.Types.VARCHAR};
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class returnedClass() {
    return Set.class;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    Set sx = (Set) x;
    Set sy = (Set) y;
    return (sx == null || sy == null) ? false : sx.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return (x != null) ? x.hashCode() : 0;
  }

  @Override
  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    String value = rs.getString(names[0]);
    Set<Long> longSet = new HashSet<>();
    if (value != null) {
      for (Iterator<String> it = SPLIT_ON_COMMA.splitToList(value).iterator(); it.hasNext(); ) {
        longSet.add(Longs.tryParse(it.next()));
      }
    }
    return longSet;
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
    return false;
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
