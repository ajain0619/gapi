package com.nexage.geneva.request.ignoredkeys;

public class FeeAdjustmentIgnoreKeys {
  public static final String[] expectedObjectCreateOrUpdateIgnoreKeys = {"pid", "version"};

  public static final String[] actualObjectCreateOrUpdateIgnoreKeys = {"pid", "version"};

  public static final String[] expectedObjectGetOrGetAllIgnoreKeys = {"version"};

  public static final String[] actualObjectGetOrGetAllIgnoreKeys = {"version"};

  private FeeAdjustmentIgnoreKeys() {}
}
