package com.ssp.geneva.sdk.onecentral.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Called User within One Central Model Response */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OneCentralUserResponse {
  @EqualsAndHashCode.Include @ToString.Include private Integer id;
  @EqualsAndHashCode.Include @ToString.Include private String firstName;
  @EqualsAndHashCode.Include @ToString.Include private String lastName;
  @EqualsAndHashCode.Include @ToString.Include private String username;
  @EqualsAndHashCode.Include @ToString.Include private String email;
  @EqualsAndHashCode.Include @ToString.Include private OneCentralUserStatus status;
  private Boolean internal;
  private Boolean systemUser;
  private Integer defaultOrgSapId;
  private Integer defaultVideoOrgId;
  private String phone;
  private Boolean primaryUserContact;
  private String defaultOrganization;
  private String totalOrganizations;
  private String contactName;
  private String contactEmail;
  private String countryCd;
  private String cdid;
  private String oktaShortId;
  private String source;
  private String last_login_date;
}
