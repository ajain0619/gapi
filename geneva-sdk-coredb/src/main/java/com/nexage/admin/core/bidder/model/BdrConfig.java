package com.nexage.admin.core.bidder.model;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bdr_config")
public class BdrConfig {

  @Setter(AccessLevel.NONE)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @EqualsAndHashCode.Include
  @Column(name = "property", length = 100)
  private String property;

  @EqualsAndHashCode.Include
  @Column(name = "value", length = 100)
  private String value;

  @Column(name = "description", length = 255)
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @PrePersist
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }
}
