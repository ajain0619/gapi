package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(
    name = "hb_partner_company",
    uniqueConstraints = @UniqueConstraint(columnNames = {"hb_partner_pid", "company_pid"}))
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HbPartnerCompany implements Serializable {

  private static final long serialVersionUID = -3596257486753530122L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @Id
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  @NotNull
  @EqualsAndHashCode.Include
  private Company company;

  @Column(name = "external_pub_id", nullable = false)
  private String externalPubId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hb_partner_pid", referencedColumnName = "pid")
  @NotNull
  @EqualsAndHashCode.Include
  private HbPartner hbPartner;
}
