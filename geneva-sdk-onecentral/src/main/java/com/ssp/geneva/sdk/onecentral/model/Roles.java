package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import java.util.Set;
import lombok.Data;

/** Defines the structure to be loaded based on environments and roles. */
@Data
public class Roles implements Serializable {
  private static final long serialVersionUID = 5485498398329443833L;
  private Set<Role> qa;
  private Set<Role> prod;
}
