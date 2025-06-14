package com.ssp.geneva.sdk.onecentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserStatus;
import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Builder(builderClassName = "OneCentralUserRequestBuilder")
@Getter
@JsonInclude(Include.NON_DEFAULT)
public class OneCentralUserRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String firstName;
  private String lastName;
  @Email private String email;
  private String username;
  private OneCentralUserStatus status;
  private long[] roleIds;
  private boolean notify;
  private String activationRedirectionUrl;
  private String systemName;
  private boolean internal;
  private boolean systemUser;
  private boolean apiUser;
  private Set<String> scopes;
  private String contactName;
  @Email private String contactEmail;
  private String phone;
}
