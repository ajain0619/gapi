package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NativeVersionTest {

  @Test
  void maxOfSetReturnsNull_whenInputIsNull() {
    NativeVersion max = NativeVersion.maxOfSet(null);
    assertNull(max);
  }

  @Test
  void maxOfSetReturnsNull_whenInputIsEmpty() {
    Set<NativeVersion> nativeVersions = Set.of();
    NativeVersion max = NativeVersion.maxOfSet(nativeVersions);
    assertNull(max);
  }

  @Test
  void maxOfSetCanHandleNullValuesInSet() {
    Set<NativeVersion> nativeVersions = Sets.newHashSet(NativeVersion.v1_1, null);
    NativeVersion max = NativeVersion.maxOfSet(nativeVersions);
    assertEquals(NativeVersion.v1_1, max);
  }

  @Test
  void maxOfSetReturnsHighestSemanticVersion_whenInputIsNonEmpty() {
    Set<NativeVersion> nativeVersions = Set.of(NativeVersion.v1_0);
    NativeVersion max = NativeVersion.maxOfSet(nativeVersions);
    assertEquals(NativeVersion.v1_0, max);

    nativeVersions = Set.of(NativeVersion.v1_0, NativeVersion.v1_1);
    max = NativeVersion.maxOfSet(nativeVersions);
    assertEquals(NativeVersion.v1_1, max);

    nativeVersions = Set.of(NativeVersion.v1_0, NativeVersion.v1_1, NativeVersion.v1_2);
    max = NativeVersion.maxOfSet(nativeVersions);
    assertEquals(NativeVersion.v1_2, max);
  }
}
