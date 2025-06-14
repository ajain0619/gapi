package com.nexage.admin.core.model.postauctiondiscount;

import com.nexage.admin.core.model.CompanyView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "buyer_seat")
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Audited
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostAuctionDiscountDspSeatView implements Serializable {

  private static final long serialVersionUID = -5088352604118994100L;

  @Id
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "name")
  @EqualsAndHashCode.Include
  private String name;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private CompanyView company;
}
