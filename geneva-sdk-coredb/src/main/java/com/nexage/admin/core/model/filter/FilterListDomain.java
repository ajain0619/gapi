package com.nexage.admin.core.model.filter;

import java.io.Serializable;
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
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deny_allow_filter_list_domain")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilterListDomain implements Serializable {

  private static final long serialVersionUID = -1351777987617776922L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer pid;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private FiilterListStatus status;

  @Column(name = "black_white_filter_list_id")
  private Integer filterListId;

  @ManyToOne
  @JoinColumn(name = "domain_id", referencedColumnName = "pid")
  private Domain domain;

  @Version private Integer version;
}
