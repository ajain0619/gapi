package com.nexage.admin.core.custom.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class JsonMapUserType implements UserType {

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public int[] sqlTypes() {
    return new int[] {java.sql.Types.VARCHAR};
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class returnedClass() {
    return Map.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    Map sx = (Map) x;
    Map sy = (Map) y;

    return (sx == null || sy == null) ? false : sx.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return (x != null) ? x.hashCode() : 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    String jsonString = rs.getString(names[0]);
    if (StringUtils.isBlank(jsonString)) {
      return null;
    } else {
      try {
        Map<String, String> map = objectMapper.readValue(jsonString, Map.class);
        return map;
      } catch (Exception e) {
        throw new HibernateException("could not read map from json string");
      }
    }
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (null == value) {
      st.setNull(index, Types.VARCHAR);
    } else {
      try {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map) value;
        if (0 == map.size()) {
          st.setNull(index, Types.VARCHAR);
        } else {
          String json = new ObjectMapper().writeValueAsString(value);
          st.setString(index, json);
        }
      } catch (Exception e) {
        throw new RuntimeException("cannot parse json string from map");
      }
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
