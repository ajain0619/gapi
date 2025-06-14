package com.nexage.geneva.request.ignoredkeys;

public class DealIgnoredKeys {

  public static final String[] expectedObjectUpdate = {
    "profiles[*].pid", "profiles[*].tagPid", "publishers[*].pid"
  };

  public static final String[] actualObjectUpdate = {
    "profiles[*].pid", "bidders[*].pid", "profiles[*].tagPid", "publishers[*].pid"
  };

  public static final String[] expectedObjectCreate = {
    "pid", "createdBy", "bidders[*].pid", "profiles[*].pid", "publishers[*].pid"
  };

  public static final String[] actualObjectCreate = {
    "bidders[*].pid", "profiles[*].pid", "dateCreated", "pid", "createdBy", "publishers[*].pid"
  };
}
