package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "authority")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Authority implements Serializable {

  private static final long serialVersionUID = 3923310763220094903L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @ToString.Include
  private Long pid;

  @Column(length = 100, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private java.util.Date lastUpdate;

  @Column(name = "ext_id", nullable = false)
  @EqualsAndHashCode.Include
  private String externalId;
}
