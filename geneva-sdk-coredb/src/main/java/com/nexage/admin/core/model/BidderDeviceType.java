package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "bidder_config_device_type")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class BidderDeviceType implements Serializable {

  private static final long serialVersionUID = 3786876641524217420L;

  @EqualsAndHashCode.Include @ToString.Include @Id @GeneratedValue private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "bidder_pid", updatable = false, insertable = false)
  private Long bidderPid;

  /** Associated bidder config. */
  @ManyToOne
  @JoinColumn(name = "bidder_pid")
  @JsonBackReference
  private BidderConfig bidderConfig;

  @Column(name = "device_type_id")
  @EqualsAndHashCode.Include
  @ToString.Include
  @NotNull
  private Integer deviceTypeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "device_type_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  @JsonIgnore
  @JsonBackReference
  private DeviceType deviceType;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", updatable = false, insertable = false)
  @JsonIgnore
  private Date updatedOn;
}
