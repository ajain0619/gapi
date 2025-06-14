package com.nexage.admin.core.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public enum NativeVersion {
  v1_0(1, 0),
  v1_1(1, 1),
  v1_2(1, 2);

  private final String value;
  private final long compareToValue;

  NativeVersion(int majorVersion, int minorVersion) {
    value = majorVersion + "." + minorVersion;
    compareToValue = (1000L * majorVersion) + minorVersion;
  }

  public String asActual() {
    return value;
  }

  public static NativeVersion fromActual(String version) {
    return fromVersionsMap.get(version);
  }

  /**
   * Returns the highest version in the set.
   *
   * @param nativeVersions {@code Set} of {@code NativeVersion} to find the maximum version from
   * @return highest {@code NativeVersion} or {@code null} if {@code nativeVersions} is {@code
   *     null}, contains only {@code null}, or is empty
   */
  public static NativeVersion maxOfSet(Set<NativeVersion> nativeVersions) {
    if (nativeVersions == null) {
      return null;
    }

    return nativeVersions.stream()
        .filter(Objects::nonNull)
        .max(Comparator.comparingLong(version -> version.compareToValue))
        .orElse(null);
  }

  private static final HashMap<String, NativeVersion> fromVersionsMap = new HashMap<>();

  static {
    for (NativeVersion version : NativeVersion.values()) {
      fromVersionsMap.put(version.value, version);
    }
  }
}
