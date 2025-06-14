package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "exchange_prod")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeProduction implements Serializable {

  private static final long serialVersionUID = 6553994788551116566L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private int pid;

  @Column(name = "regional_id")
  @ToString.Include
  private int regionalId;

  @Column(length = 128, nullable = false)
  @ToString.Include
  private String name;
}
