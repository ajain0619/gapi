package com.nexage.geneva.request.ignoredkeys;

public class UserIgnoredKeys {
  public static final String[] expectedObjectCreateUser = {};
  public static final String[] actualObjectCreateUser = {"pid", "id", "version", "companyName"};

  public static final String[] expectedObjectUpdateUser = {"version"};
  public static final String[] actualObjectUpdateUser = {"version"};

  public static final String[] expectedObjectSearchUser = {"version"};
  public static final String[] actualObjectSearchUser = {"version"};
}
