package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_type")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeviceType implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name")
  @EqualsAndHashCode.Include
  private String name;
}
