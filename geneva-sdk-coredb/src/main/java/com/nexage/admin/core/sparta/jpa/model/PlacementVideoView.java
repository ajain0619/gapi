package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "placement_video")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlacementVideoView implements Serializable {

  private static final long serialVersionUID = -4839802949507440110L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "longform", nullable = false)
  @ColumnDefault("false")
  @NotNull
  private boolean longform;
}
