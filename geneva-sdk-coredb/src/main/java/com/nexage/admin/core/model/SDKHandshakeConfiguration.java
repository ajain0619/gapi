package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Audited
@Table(name = "sdk_handshake_configuration")
public class SDKHandshakeConfiguration implements Serializable {

  private static final long serialVersionUID = -3600965360336356132L;

  @Id
  @Column(name = "pid", nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "handshake_key", nullable = false, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String handshakeKey;

  @Column(name = "handshake_value", nullable = false, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String handshakeValue;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @ToString.Include
  private Date updatedOn;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }
}
