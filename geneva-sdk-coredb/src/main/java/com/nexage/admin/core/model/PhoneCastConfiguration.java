package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "phonecast_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PhoneCastConfiguration implements Serializable {

  private static final long serialVersionUid = 1L;

  @Id
  @Column(name = "id", nullable = false, length = 32, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;

  @Version
  @Column(name = "VERSION", nullable = false)
  @ToString.Include
  private Integer version;

  @Column(name = "prefix", length = 64)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String prefix;

  @Column(name = "config_key")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String configKey;

  @Column(name = "config_value", columnDefinition = "text")
  @EqualsAndHashCode.Include
  private String configValue;

  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @Column(name = "lastUpdate")
  @EqualsAndHashCode.Exclude
  private Date lastUpdate;
}
