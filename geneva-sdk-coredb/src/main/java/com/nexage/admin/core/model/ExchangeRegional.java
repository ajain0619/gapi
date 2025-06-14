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

/** Hibernate model for the reference table exchange_regional */
@Entity
@Table(name = "exchange_regional")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeRegional implements Serializable {

  private static final long serialVersionUID = -2836738049966132225L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  /** Short string name, e.g. USA */
  @Column(length = 32, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;
}
