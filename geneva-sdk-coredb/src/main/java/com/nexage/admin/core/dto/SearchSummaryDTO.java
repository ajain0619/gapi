package com.nexage.admin.core.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public abstract class SearchSummaryDTO {

  @EqualsAndHashCode.Include @ToString.Include protected final long pid;
  @EqualsAndHashCode.Include @ToString.Include protected final String name;
  @EqualsAndHashCode.Include @ToString.Include protected final Type type;

  public enum Type {
    SELLER,
    BUYER,
    SITE,
    SEATHOLDER
  }
}
