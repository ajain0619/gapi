package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

class JpaPolyfillsTest {
  private Map<String, String> aliasMap;
  Sort.Order sortOrder;
  Sort sort;
  private static String aliasName = "a";

  @BeforeEach
  public void setUp() {
    aliasMap = new HashMap<>();
    sortOrder = new Sort.Order(Sort.Direction.DESC, "name");
    sort = Sort.by(sortOrder);
  }

  @Test
  void shouldCreatePageWithNoSort() {
    aliasMap.put("name", aliasName);
    Pageable pageable = PageRequest.of(0, 1);
    Pageable newPageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, aliasMap);
    assertEquals(pageable, newPageable);
  }

  @Test
  void shouldCreatePageWithNoAliasMap() {
    Pageable pageable = PageRequest.of(0, 1, sort);
    Pageable newPageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, null);
    assertEquals(pageable, newPageable);
  }

  @Test
  void shouldCreatePageWithNonMatchingAlias() {
    aliasMap.put("something", aliasName);
    Pageable pageable = PageRequest.of(0, 1, sort);
    Pageable newPageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, aliasMap);
    assertEquals(sortOrder.getDirection(), newPageable.getSort().iterator().next().getDirection());
    assertEquals(sortOrder.getProperty(), newPageable.getSort().iterator().next().getProperty());
  }

  @Test
  void shouldCreatePageWithMultiSortAndMatchingProperties() {
    aliasMap.put("name", aliasName);
    aliasMap.put("revenue", aliasName);
    Sort.Order sortOrder2 = new Sort.Order(Sort.Direction.ASC, "revenue");
    List<Sort.Order> orders = new ArrayList<>();
    orders.add(sortOrder);
    orders.add(sortOrder2);
    Pageable pageable = PageRequest.of(0, 1, Sort.by(orders));
    Pageable newPageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, aliasMap);
    Iterator<Sort.Order> it = newPageable.getSort().iterator();
    Sort.Order newSortOrder1 = it.next();
    Sort.Order newSortOrder2 = it.next();
    String expectedSortName = aliasName + "." + sortOrder.getProperty();
    String expectedSortName2 = aliasName + "." + sortOrder2.getProperty();
    assertEquals(sortOrder.getDirection(), newSortOrder1.getDirection());
    assertEquals(expectedSortName, newSortOrder1.getProperty());
    assertEquals(sortOrder2.getDirection(), newSortOrder2.getDirection());
    assertEquals(expectedSortName2, newSortOrder2.getProperty());
    assertEquals(pageable.getPageNumber(), newPageable.getPageNumber());
    assertEquals(pageable.getPageSize(), newPageable.getPageSize());
  }

  @Test
  void shouldCreatePageWithSortListContainingGivenProperty() {
    Pageable pageable = PageRequest.of(1, 10);
    Pageable newPageable = JpaPolyfills.addSortBy(pageable, "property");
    List<Order> orders = newPageable.getSort().stream().collect(Collectors.toList());
    assertEquals(1, orders.size());
    assertEquals("property", orders.get(0).getProperty());
    assertEquals(Direction.ASC, orders.get(0).getDirection());
  }

  @Test
  void shouldCreatePageWithSortListExtendedWithGivenProperty() {
    Pageable pageable =
        PageRequest.of(1, 10, Sort.by(Order.asc("property1"), Order.desc("property2")));
    Pageable newPageable = JpaPolyfills.addSortBy(pageable, "property3");
    List<Order> orders = newPageable.getSort().stream().collect(Collectors.toList());
    assertEquals(3, orders.size());
    assertEquals("property1", orders.get(0).getProperty());
    assertEquals(Direction.ASC, orders.get(0).getDirection());
    assertEquals("property2", orders.get(1).getProperty());
    assertEquals(Direction.DESC, orders.get(1).getDirection());
    assertEquals("property3", orders.get(2).getProperty());
    assertEquals(Direction.ASC, orders.get(2).getDirection());
  }
}
