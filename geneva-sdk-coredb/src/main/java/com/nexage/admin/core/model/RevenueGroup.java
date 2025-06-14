package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "revenue_group")
@Audited
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RevenueGroup implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "revenue_group_name", unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Version private Integer version;

  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(name = "created_on")
  private Date createdOn;

  @PrePersist
  @PreUpdate
  public void setTimestamps() {
    updatedOn = Date.from(Instant.now());
    createdOn = ObjectUtils.firstNonNull(createdOn, updatedOn);
  }
}
