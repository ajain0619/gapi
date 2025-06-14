package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class BlockListInclusionTest {
  @Test
  void test_blockListInclusionZeroValue() {
    assertEquals(BlockListInclusion.NO, BlockListInclusion.fromInt(0));
  }

  @Test
  void test_blockListInclusionOneValue() {
    assertEquals(BlockListInclusion.YES, BlockListInclusion.fromInt(1));
  }

  @Test
  void test_blockListInclusionTwoValue() {
    assertEquals(BlockListInclusion.ADAPTIVE, BlockListInclusion.fromInt(2));
  }

  @Test
  void test_outOfBoundsBlockListInclusionIsNull() {
    assertNull(BlockListInclusion.fromInt(12));
  }
}
