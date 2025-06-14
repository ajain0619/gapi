package com.nexage.app.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BdrAdvertiserDTO implements Serializable {

  public static final String ACTIVE = "ACTIVE";

  @EqualsAndHashCode.Include private Long pid;

  @EqualsAndHashCode.Include private String name;

  @EqualsAndHashCode.Include private String status = ACTIVE;

  @EqualsAndHashCode.Include private String domainName;

  @EqualsAndHashCode.Include private Long companyPid;

  @EqualsAndHashCode.Include private String iabCategory;
}
