package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entitlement implements Serializable {
  private static final long serialVersionUID = 1L;
  private int id;
  private String name;
  private String displayName;
  private String application;
  private String type;
  private String permission;
}
