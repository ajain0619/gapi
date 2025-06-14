package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;

@Immutable
@Entity
@Data
@Table(name = "company")
@Where(clause = "type = 'BUYER'")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RuleDSPBiddersView implements Serializable {

  private static final long serialVersionUID = -2994574402606615360L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", referencedColumnName = "pid")
  private Set<DealBidderConfigView> bidders;
}
