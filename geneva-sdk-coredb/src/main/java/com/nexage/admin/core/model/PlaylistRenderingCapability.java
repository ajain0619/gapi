package com.nexage.admin.core.model;

import com.nexage.admin.core.custom.listener.ReadOnlyGuardEntityListener;
import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

/** Model representation of sdk_capability table. */
@Entity
@Table(name = "sdk_capability")
@EntityListeners(ReadOnlyGuardEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlaylistRenderingCapability implements Serializable {

  private static final long serialVersionUID = 4121961779541505795L;

  @Id
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on", nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Date createdOn;

  @Column(name = "status", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @ToString.Include
  private Status status;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Date updatedOn;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  // domain fields

  @Column(name = "display_value", nullable = false, unique = true)
  @ToString.Include
  private String displayValue;

  @Column(name = "value", nullable = false, unique = true)
  @ToString.Include
  private String value;
}
