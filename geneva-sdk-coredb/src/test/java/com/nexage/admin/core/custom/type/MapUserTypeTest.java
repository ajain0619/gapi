package com.nexage.admin.core.custom.type;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapUserTypeTest {

  private MapUserType mapUserType;

  @BeforeEach
  public void setup() {
    mapUserType = new MapUserType();
  }

  @Test
  void mapUserGetShouldPassWithNull() throws Exception {
    var input = "1=foo,2=bar";
    var mockResultSet = mock(ResultSet.class);
    when(mockResultSet.getString("col1")).thenReturn(input);
    var colNames = new String[] {"col1"};
    var session = mock(SharedSessionContractImplementor.class);
    var out = mapUserType.nullSafeGet(mockResultSet, colNames, session, new Object());

    assertTrue(out instanceof Map);
    var outMap = (Map<Long, String>) out;
    assertAll(
        "map data",
        () -> assertTrue(outMap.containsKey(1L)),
        () -> assertEquals("foo", outMap.get(1L)),
        () -> assertTrue(outMap.containsKey(2L)),
        () -> assertEquals("bar", outMap.get(2L)));
  }

  @Test
  void nullSafeSetTest() {
    var mockPs = mock(PreparedStatement.class);
    var session = mock(SharedSessionContractImplementor.class);
    Object object = new Object();
    assertThrows(
        HibernateException.class, () -> mapUserType.nullSafeSet(mockPs, object, 0, session));
  }

  @Test
  void deepCopyTest() {
    var input = new Object();
    var out = mapUserType.deepCopy(input);
    assertEquals(input, out);
  }

  @Test
  void hashCodeTest() {
    var input = new Object();
    var out = mapUserType.hashCode(input);
    assertEquals(input.hashCode(), out);
  }

  @Test
  void equalsTest() {
    var out = mapUserType.equals(null, null);
    assertFalse(out);

    var map1 = new HashMap<String, String>();
    var map2 = new HashMap<String, String>();
    out = mapUserType.equals(map1, map2);
    assertTrue(out);
  }
}
