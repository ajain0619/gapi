package com.nexage.admin.core.dto;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Immutable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SiteSummaryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @ToString.Include private String id;
  @EqualsAndHashCode.Include @ToString.Include private Long pid;
  @EqualsAndHashCode.Include @ToString.Include private String url;
  @EqualsAndHashCode.Include @ToString.Include private String name;
  @EqualsAndHashCode.Include @ToString.Include private String globalAliasName;
  @EqualsAndHashCode.Include @ToString.Include private Type type;
  @EqualsAndHashCode.Include @ToString.Include private Platform platform;
  @EqualsAndHashCode.Include @ToString.Include private Status status;
  @EqualsAndHashCode.Include @ToString.Include private Boolean live;
  @EqualsAndHashCode.Include @ToString.Include private Long sellerPid;
  @EqualsAndHashCode.Include @ToString.Include private String sellerName;
  @EqualsAndHashCode.Include @ToString.Include private String domain;
}
