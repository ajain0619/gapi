package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RtbProfileLibraryTest {
  private RtbProfileLibrary rtbProfileLibrary;

  @BeforeEach
  void setup() {
    rtbProfileLibrary = new RtbProfileLibrary();
  }

  @Test
  void shouldSetGroups() {
    Set<RtbProfileGroup> groupSet = new HashSet<>();
    groupSet.add(new RtbProfileGroup());
    rtbProfileLibrary.setGroups(groupSet);

    assertEquals(groupSet.size(), rtbProfileLibrary.getGroups().size());
  }
}
