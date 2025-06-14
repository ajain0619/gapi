package com.nexage.admin.core.model.filter;

import com.nexage.admin.core.model.AppBundleData;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deny_allow_filter_list_app_bundle_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilterListAppBundle implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer pid;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private FiilterListStatus status;

  @Column(name = "deny_allow_filter_list_id")
  private Integer filterListId;

  @ManyToOne
  @JoinColumn(name = "app_bundle_data_id", referencedColumnName = "pid")
  private AppBundleData app;

  @Version private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  private Date createdOn;
}
