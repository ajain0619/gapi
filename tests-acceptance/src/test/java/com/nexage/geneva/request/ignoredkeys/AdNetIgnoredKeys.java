package com.nexage.geneva.request.ignoredkeys;

public class AdNetIgnoredKeys {
  public static final String[] expectedObjectCreate = {"partnerId", "pid"};

  public static final String[] actualObjectCreate = {
    "partnerId",
    "pid",
    "sitePids",
    "id",
    "creationDate",
    "version",
    "tags",
    "companyPid",
    "lastUpdate",
    "exchange",
    "adType",
    "reportAuthType",
    "paramMetadata"
  };

  public static final String[] actualAndExpectedObjectUpdate = {
    "version", "lastUpdate", "postTemplate"
  };
}
