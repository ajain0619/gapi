package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "attribute_values")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class InventoryAttributeValue implements Serializable {

  private static final long serialVersionUID = -4029290001230297647L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "name", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "is_enabled", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean isEnabled;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false, updatable = true)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private Date lastUpdate;

  @ManyToOne
  @JoinColumn(name = "attribute_pid", referencedColumnName = "pid")
  @ToString.Include
  private InventoryAttribute attribute;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  @PrePersist
  @PreUpdate
  public void setLastUpdate() {
    this.lastUpdate = new Date();
  }
}
