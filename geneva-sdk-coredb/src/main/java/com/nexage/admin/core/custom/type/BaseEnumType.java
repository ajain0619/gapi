package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.HasInt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class BaseEnumType<T extends Enum<T> & HasInt<T>> extends BaseType<T> {
  private static final int[] SQL_TYPES = {Types.INTEGER};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public T nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {

    int intValue = rs.getInt(names[0]);
    if (!rs.wasNull()) {
      return fromInt(intValue);
    }
    return null;
  }

  private T fromInt(int value) {
    return EnumSet.allOf(returnedClass()).stream()
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("Congrats! You've got an enum with no single value!"))
        .fromInt(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value != null) {
      st.setInt(index, ((T) value).asInt());
    } else {
      st.setNull(index, Types.INTEGER);
    }
  }
}
