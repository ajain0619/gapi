package com.nexage.app.util.placement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlacementValidMemoGeneratorTest {

  private String memo;

  @Test
  void testGenerateValidParams() {
    memo = PlacementValidMemoGenerator.generate(123L, "memo");
    assertTrue(memo.startsWith(String.format("memo-s%d-t", 123L)));

    memo = PlacementValidMemoGenerator.generate(275L, "memo");
    assertTrue(memo.startsWith(String.format("memo-s%d-t", 275L)));

    memo = PlacementValidMemoGenerator.generate(275L, "memo4");
    assertTrue(memo.startsWith(String.format("memo4-s%d-t", 275L)));

    memo = PlacementValidMemoGenerator.generate(275L, "memo4foo");
    assertTrue(memo.startsWith(String.format("memo4foo-s%d-t", 275L)));

    memo = PlacementValidMemoGenerator.generate(275L, "memo-s10000186-t435946");
    assertTrue(memo.startsWith(String.format("memo-s%d-t", 275L)));

    memo = PlacementValidMemoGenerator.generate(275L, "memo-s10000186-t435946-s10000186-t435946");
    assertTrue(memo.startsWith(String.format("memo-s%d-t", 275L)));

    memo = PlacementValidMemoGenerator.generate(275L, "undefined");
    assertTrue(memo.startsWith(String.format("pl-s%d-t", 275L)));
  }

  @Test
  void testGenerateInvalidParams() {
    memo = PlacementValidMemoGenerator.generate(null, "memo");
    assertEquals("memo", memo);

    memo = PlacementValidMemoGenerator.generate(123L, null);
    assertTrue(memo.startsWith(String.format("pl-s%d-t", 123L)));

    memo = PlacementValidMemoGenerator.generate(null, null);
    assertEquals("pl", memo);
  }
}
