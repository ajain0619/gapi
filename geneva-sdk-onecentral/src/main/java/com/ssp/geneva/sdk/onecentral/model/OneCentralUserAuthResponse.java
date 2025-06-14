package com.ssp.geneva.sdk.onecentral.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OneCentralUserAuthResponse {
  @EqualsAndHashCode.Include @ToString.Include String username;
  String firstName;
  String lastName;
  String email;
  @EqualsAndHashCode.Include @ToString.Include Boolean internal;
  Boolean systemUser;
  Boolean impersonationMode;
  String impersonationActualUsername;
  String sessionToken;
  String sessionTokenType;
  String countryCd;
  @EqualsAndHashCode.Include @ToString.Include String status;
  String cdid;
  String oktaShortId;
  Integer defaultOrgSapId;
  Integer defaultVideoOrgId;
  @EqualsAndHashCode.Include @ToString.Include Entitlement[] entitlements;
  AgencyAdvertiserAssociation[] agencyAdvertiserAssociations;
  String uri;
  UserPreference[] userPreferences;

  @Data
  static class Entitlement {
    Integer id;
    @EqualsAndHashCode.Include @ToString.Include String name;
    String displayName;
    String application;
    @EqualsAndHashCode.Include @ToString.Include String type;
    @EqualsAndHashCode.Include @ToString.Include String permission;
    Long[] organizationIds;
  }

  @Data
  static class AgencyAdvertiserAssociation {
    Long agency;
    Long advertiser;
  }

  @Data
  static class UserPreference {
    Integer id;
    String preference;
    String value;
    String expirationDate;
    String type;
  }
}
