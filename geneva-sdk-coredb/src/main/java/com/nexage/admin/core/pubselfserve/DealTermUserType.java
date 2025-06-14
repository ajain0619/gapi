package com.nexage.admin.core.pubselfserve;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class DealTermUserType implements UserType {

  private static final int[] SQL_TYPES = {Types.VARCHAR};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class returnedClass() {
    return DealTerm.class;
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
    String value = rs.getString(names[0]);
    DealTerm result = null;
    BigDecimal revShare = null;
    BigDecimal rtbFee = null;
    if (!rs.wasNull() && !StringUtils.isBlank(value)) {
      String[] values = value.split(",");
      if (values.length > 1) {
        try {
          if (StringUtils.isNotBlank(values[0])) {
            revShare = new BigDecimal(values[0]);
          }
        } catch (NumberFormatException nfe) {
        }
        try {
          if (StringUtils.isNotBlank(values[1])) {
            rtbFee = new BigDecimal(values[1]);
          }
        } catch (NumberFormatException nfe) {
        }

        if (null != revShare || null != rtbFee) {
          result = new DealTerm(revShare, rtbFee);
        }
      }
    }
    return result;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {}

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return null;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return false;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) {
    return false;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return false;
  }
}
