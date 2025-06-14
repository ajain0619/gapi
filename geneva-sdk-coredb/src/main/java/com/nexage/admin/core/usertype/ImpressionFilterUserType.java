package com.nexage.admin.core.usertype;

import com.nexage.admin.core.enums.ImpressionFilter;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class ImpressionFilterUserType implements UserType {

  private static final int[] SQL_TYPES = {Types.VARCHAR};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class<ImpressionFilter> returnedClass() {
    return ImpressionFilter.class;
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
    Set<ImpressionFilter> result = EnumSet.noneOf(ImpressionFilter.class);
    if (value != null) {
      String[] imprs = value.split(",");
      for (String impr : imprs) {
        result.add(ImpressionFilter.fromActual(impr));
      }
    }
    return result;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (null == value) {
      st.setObject(index, null);
    } else {
      @SuppressWarnings("unchecked")
      Set<ImpressionFilter> filters = (Set<ImpressionFilter>) value;
      if (filters.isEmpty()) {
        st.setObject(index, null);
      } else {
        List<String> strFilters = new ArrayList<>();
        for (ImpressionFilter filter : filters) {
          strFilters.add(filter.asActual());
        }
        st.setObject(index, StringUtils.join(strFilters, ","));
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
