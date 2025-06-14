package com.nexage.geneva.request.ignoredkeys;

public class BidderConfigIgnoredKeys {

  public static final String[] expectedObjectCreate = {
    "version", "pid",
    //            "bidderName",
  };

  public static final String[] actualObjectCreate = {
    "version", "pid",
    //            "bidderName"
  };

  public static final String[] expectedObjectCreateFromNewCompany = {"[*].id", "[*].pid"};

  public static final String[] actualObjectCreateFromNewCompany = {"[*].id", "[*].pid"};
}
