package com.nexage.geneva.request.ignoredkeys;

public class AdSourceDefaultsIgnoredKeys {
  public static final String[] expectedObjectCreateMediation = {"pid", "version"};
  public static final String[] actualObjectCreateMediation = {"pid", "version"};

  public static final String[] expectedObjectUpdateMediation = {"version"};
  public static final String[] actualObjectUpdateMediation = {"version"};

  public static final String[] expectedObjectGetMediation = {"version"};
  public static final String[] actualObjectGetMediation = {"version"};

  public static final String[] expectedObjectUpdateRtb = {"attributes.version"};
  public static final String[] actualObjectUpdateRtb = {"attributes.version"};
}
