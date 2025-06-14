package com.nexage.geneva.util.geneva;

/** Enum represents statuses that can be selected by user in Ad Screening. */
public enum AdScreeningStatus {
  ALL("Allowed", "0"),
  ALLOWED("Allowed", "2"),
  BLOCKED("Blocked", "1"),
  EMPTY("Empty", ""),
  INVALID("Invalid", "invalid");
  private String name, requestParameter;

  AdScreeningStatus(String drillDownName, String requestParameter) {
    this.name = drillDownName;
    this.requestParameter = requestParameter;
  }

  public String getName() {
    return name;
  }

  public String getRequestParameter() {
    return requestParameter;
  }

  public static AdScreeningStatus getAdScreeningStatus(String name) {
    AdScreeningStatus result = null;

    for (AdScreeningStatus adScreeningStatus : values()) {
      if (adScreeningStatus.getName().equals(name)) {
        result = adScreeningStatus;
        break;
      }
    }
    return result;
  }
}
