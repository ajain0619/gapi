package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.util.MapSplitter;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapUserType implements UserType {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final MapSplitter mapSplitter =
      MapSplitter.separator(",").withKeyValueSeparator("=");

  public int[] sqlTypes() {
    return new int[] {java.sql.Types.VARCHAR};
  }

  @SuppressWarnings("rawtypes")
  public Class returnedClass() {
    return Map.class;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    Map sx = (Map) x;
    Map sy = (Map) y;

    return (sx == null || sy == null) ? false : sx.equals(y);
  }

  public int hashCode(Object x) throws HibernateException {
    return (x != null) ? x.hashCode() : 0;
  }

  @Override
  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    String keyValuePairs = rs.getString(names[0]);
    return mapSplitter.splitAsLong(keyValuePairs);
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    throw new HibernateException("Unable to update formula column.");
  }

  @SuppressWarnings("unchecked")
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public boolean isMutable() {
    return false;
  }

  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}
