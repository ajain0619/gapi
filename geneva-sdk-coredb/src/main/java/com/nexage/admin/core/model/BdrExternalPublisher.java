package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** The persistent class for the bdr_publisher_info database table. */
@Table(name = "bdr_publisher_info")
@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BdrExternalPublisher implements Serializable {

  private static final long serialVersionUID = 567L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "pub_alias")
  @ToString.Include
  private String pubAlias;

  @Column(name = "pub_name_alias")
  @ToString.Include
  private String pubNameAlias;

  @Column(name = "exchange_ext_id")
  @ToString.Include
  private String exchangeExtId;

  @ToString.Include private Boolean isInTarget;
}
