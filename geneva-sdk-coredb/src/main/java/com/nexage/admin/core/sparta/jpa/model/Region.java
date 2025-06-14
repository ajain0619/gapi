package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.model.Company;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

/** Region that a element(s) is assigned to. */
@Entity
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "region")
public class Region implements Serializable {

  private static final long serialVersionUID = 3846706313214254562L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "network")
  @Size(max = 50)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private String network;

  @Version
  @Column(name = "version")
  @ToString.Include
  private Integer version;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @ToString.Include
  private Date updatedOn;

  @JsonIgnore
  @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id")
  private Set<Company> companies = new HashSet<>();

  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }
}
