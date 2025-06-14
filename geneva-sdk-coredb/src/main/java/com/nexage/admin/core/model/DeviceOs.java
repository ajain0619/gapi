package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "device_os")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOs implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "pid", nullable = false)
  private Long pid;

  @Column(name = "name")
  @NonNull
  private String name;
}
