package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Getter
@Immutable
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyMdmView implements Serializable {

  private static final long serialVersionUID = 1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @ToString.Include
  @EqualsAndHashCode.Include
  private Long pid;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private List<CompanyMdmId> mdmIds;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_seat_id")
  private SellerSeatMdmView sellerSeat;
}
