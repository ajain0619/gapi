package com.nexage.geneva.request.ignoredkeys;

/**
 * Class with keys which should be ignored during comparing responses of different operations with
 * sites
 */
public class ReportingApiIgnoredKeys {
  public static final String[] expectedObjectGet = {
    "detail->bots", "detail->fraud", "summary->bots", "summary->fraud"
  };

  public static final String[] actualObjectGet = {
    "detail->bots", "detail->fraud", "summary->bots", "summary->fraud"
  };
}
