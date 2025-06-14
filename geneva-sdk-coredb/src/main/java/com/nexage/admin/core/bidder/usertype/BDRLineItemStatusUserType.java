package com.nexage.admin.core.bidder.usertype;

import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class BDRLineItemStatusUserType implements UserType {

  private static final int[] SQL_TYPES = {Types.INTEGER};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class<BDRLineItemStatus> returnedClass() {
    return BDRLineItemStatus.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) return true;
    if (null == x || null == y) return false;
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
    int value = rs.getInt(names[0]);
    BDRLineItemStatus result = null;
    if (!rs.wasNull()) {
      result = BDRLineItemStatus.fromInt(value);
    }
    return result;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (null == value) {
      throw new RuntimeException("Enum cannot be null");
    } else {
      BDRLineItemStatus status = ((BDRLineItemStatus) value);
      int statusVal =
          status == BDRLineItemStatus.SCHEDULED
              ? BDRLineItemStatus.ACTIVE.asInt()
              : status.asInt(); // TODO KS-2809
      st.setInt(index, statusVal);
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
