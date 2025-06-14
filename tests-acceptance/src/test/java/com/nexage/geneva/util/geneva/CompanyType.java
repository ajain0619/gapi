package com.nexage.geneva.util.geneva;

/** Enum represents company types that can be selected by user in Reports. */
public enum CompanyType {
  SELLER("Seller", "company"),
  SEATHOLDER("Seat Holder", "seatholder"),
  BUYER("Buyer", "buyer");
  private String name, requestParameter;

  CompanyType(String name, String requestParameter) {
    this.name = name;
    this.requestParameter = requestParameter;
  }

  public String getName() {
    return name;
  }

  public String getRequestParameter() {
    return requestParameter;
  }

  public static CompanyType getCompanyType(String name) {
    CompanyType result = null;

    for (CompanyType companyType : values()) {
      if (companyType.getName().equals(name)) {
        result = companyType;
        break;
      }
    }

    return result;
  }
}
