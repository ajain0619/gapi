package com.nexage.admin.core.model;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CustomerTypeTargetingTest {

  @Test
  void shouldEqualExpectedCustomerTypeEnumerator() {
    assertEquals(4, CustomerTypeTargeting.values().length);

    List<String> names =
        Arrays.stream(CustomerTypeTargeting.values()).map(Enum::name).collect(toList());
    assertTrue(names.contains("RESELLER"));
    assertTrue(names.contains("O_AND_O"));
    assertTrue(names.contains("DIRECT"));
    assertTrue(names.contains("BOTH"));
  }
}
