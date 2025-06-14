package com.ssp.geneva.sdk.onecentral.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class OneCentralUserRolesResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long roleId;
  private String id;
  private String application;
  private String type;
  private List<Entitlement> entitlements;
}
