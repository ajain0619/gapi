package com.nexage.admin.dw.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.dw.geneva.util.MurmurHash3;
import org.junit.jupiter.api.Test;

class MurmurHash3Test {
  private static final int MURMUR_HASH_3_SEED = 1540483477;

  @Test
  void testByteArrayMurmurhash3_x86_32() {
    assertEquals(
        -1601135203,
        MurmurHash3.murmurhash3_x86_32(
            "Test Small Data".getBytes(),
            0,
            "Test Small Data".getBytes().length,
            MURMUR_HASH_3_SEED));
    assertEquals(
        -1865663308,
        MurmurHash3.murmurhash3_x86_32(
            "Test Really Long Data .............. End".getBytes(),
            0,
            "Test Really Long Data .............. End".getBytes().length,
            MURMUR_HASH_3_SEED));
  }

  @Test
  void testStringMurmurhash3_x86_32() {
    assertEquals(
        618609003,
        MurmurHash3.murmurhash3_x86_32(
            "Test Short String", 0, "Test Short String".length(), MURMUR_HASH_3_SEED));
    assertEquals(
        885799610,
        MurmurHash3.murmurhash3_x86_32(
            "Test Really Long String .............. End",
            0,
            "Test Really Long String .............. End".length(),
            MURMUR_HASH_3_SEED));
  }

  @Test
  void testByteArrayMurmurhash3_x64_128() {
    var result = new MurmurHash3.LongPair();

    MurmurHash3.murmurhash3_x64_128(
        "Test Small Data".getBytes(),
        0,
        "Test Small Data".getBytes().length,
        MURMUR_HASH_3_SEED,
        result);
    assertEquals(-1003556712971785959L, result.val1);
    assertEquals(2824284071832872175L, result.val2);

    MurmurHash3.murmurhash3_x64_128(
        "Test Really Long Data .............. End".getBytes(),
        0,
        "Test Really Long Data .............. End".getBytes().length,
        MURMUR_HASH_3_SEED,
        result);
    assertEquals(-5437935385761757333L, result.val1);
    assertEquals(6172427182869561933L, result.val2);
  }
}
