package com.nexage.app.dto;

import com.nexage.admin.core.enums.Status;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RevenueGroupDTO implements Serializable {

  @Serial private static final long serialVersionUID = 4500790047116206351L;

  @ToString.Include @EqualsAndHashCode.Include private Long pid;

  @EqualsAndHashCode.Include private String id;

  @ToString.Include @EqualsAndHashCode.Include private String name;

  @EqualsAndHashCode.Include private Status status;

  @EqualsAndHashCode.Include private Integer version;
}
