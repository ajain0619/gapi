package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserMigrationRequest implements Serializable {

  long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @NonNull @NotNull private String firstName;
  @EqualsAndHashCode.Include @NonNull @NotNull private String lastName;
  @EqualsAndHashCode.Include @NonNull @NotNull @Email private String email;
  @EqualsAndHashCode.Include @NonNull @NotNull private OneCentralUserStatus status;
  @ToString.Exclude private String password;
  private String username;
  private boolean internal;
  private long[] roleIds;
  private String phone;
  private String countryCd;
  private boolean notify;
  private Integer organizationId;
  private String activationRedirectionUrl;
  @Email private String supportEmail;
  private String systemName;
}
