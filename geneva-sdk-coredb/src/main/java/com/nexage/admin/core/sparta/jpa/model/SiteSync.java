package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class is used as a read-only model to access only necessary fields in the Site table for the DwSync job
 *
 */
@Entity
@Immutable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "site")
public class SiteSync implements Serializable {

  private static final long serialVersionUID = 3522483739135534007L;

  @Getter
  // this is an inner class to allow the same interface as Site
  public class Company {
    Long pid = 0L;

    public Company setPid(Long pid) {
      this.pid = pid;
      return this;
    }
  }

  @Transient private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Column(nullable = false, updatable = false)
  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "dcn", nullable = false, updatable = false, insertable = false, unique = true)
  @Size(max = 32)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String dcn;

  @Column(name = "id", nullable = false, updatable = false, insertable = false, unique = true)
  @Size(max = 32)
  @ToString.Include
  private String id;

  @Column(name = "last_update", nullable = false, updatable = false, insertable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @ToString.Include
  private Date lastUpdate;

  @Column(nullable = false, unique = true, updatable = false, insertable = false)
  @Size(max = 255)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "status", nullable = false, updatable = false, insertable = false)
  @ToString.Include
  private Integer statusVal;

  @Column(name = "company_pid", nullable = false, updatable = false, insertable = false)
  @ToString.Include
  private Long companyPid;

  @Transient @ToString.Include private Company company = new Company();
}
