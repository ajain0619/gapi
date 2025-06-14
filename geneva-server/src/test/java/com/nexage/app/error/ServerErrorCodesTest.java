package com.nexage.app.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServerErrorCodesTest {

  @Test
  void shouldValidateIfServerErrorCodesAreUnique() {
    Set<Integer> duplicates = Sets.newHashSet();
    Set<Integer> sink = Sets.newHashSet();

    EnumSet.allOf(ServerErrorCodes.class).stream()
        .map(ServerErrorCodes::getCode)
        .forEach(
            e -> {
              if (sink.contains(e)) {
                duplicates.add(e);
              } else {
                sink.add(e);
              }
            });

    assertEquals(0, duplicates.size(), "Duplicate codes:" + duplicates.toString());
  }
}
