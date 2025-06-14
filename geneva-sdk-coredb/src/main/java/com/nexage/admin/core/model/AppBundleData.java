package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "app_bundle_data")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AppBundleData extends BaseAppBundleAndAlias implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "app_bundle_id")
  private String appBundleId;
}
