package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the exchange_config database table. */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "exchange_config")
public class ExchangeConfig implements Serializable {

  private static final long serialVersionUID = 7016165276387586712L;

  @Id
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private java.util.Date updatedOn;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String property;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String value;

  @Column private String description;
}
