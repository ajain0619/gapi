package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import lombok.Data;

/** Defines a OneCentral Role. */
@Data
public class Role implements Serializable {
  private static final long serialVersionUID = 5773632315155381381L;
  private Long id;
  private String name;
}
