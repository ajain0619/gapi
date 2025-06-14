package com.nexage.geneva.request.ignoredkeys;

public class RtbProfileLibrariesIgnoredKeys {

  public static final String[] expectedObjectUpdate = {
    "version", "groups[*].pid", "groups[*].version"
  };

  public static final String[] actualObjectUpdate = {
    "version", "groups[*].pid", "groups[*].version"
  };

  public static final String[] expectedObjectCreate = {"version", "groups[*].version"};

  public static final String[] actualObjectCreate = {
    "pid", "version", "groups[*].pid", "groups[*].version"
  };

  public static final String[] expectedObjectClone = {
    "version", "groups[*].pid", "groups[*].version", "groups[*].name"
  };

  public static final String[] actualObjectClone = {
    "version", "groups[*].pid", "groups[*].version", "groups[*].name"
  };
}
