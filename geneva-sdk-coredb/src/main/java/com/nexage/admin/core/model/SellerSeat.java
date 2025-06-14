package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "seller_seat")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SellerSeat implements Serializable {

  private static final long serialVersionUID = -4876645805279122826L;

  public static final boolean ENABLED = true;
  public static final boolean DISABLED = false;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "name", nullable = false, length = 100)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "status", nullable = false)
  @NotNull
  private boolean status;

  @OneToMany(mappedBy = "sellerSeat", cascade = CascadeType.MERGE)
  private Set<Company> sellers = new HashSet<>();

  @Version
  @Column(name = "version", nullable = false)
  @NotNull
  private Integer version;

  @Column(name = "created_by", updatable = false)
  private Long createdBy;

  @Column(name = "updated_on", updatable = false, insertable = false)
  private Date updatedOn;

  @OneToMany(mappedBy = "sellerSeat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnore
  @Getter
  @Setter
  private List<SellerSeatMdmId> mdmIds = new ArrayList<>();

  public void enable() {
    setStatus(ENABLED);
  }

  public void disable() {
    setStatus(DISABLED);
  }

  public boolean isDisabled() {
    return status == DISABLED;
  }

  public void addSeller(Company seller) {
    this.sellers.add(seller);
    seller.setSellerSeat(this);
  }

  public void removeSeller(Company seller) {
    this.sellers.remove(seller);
    seller.setSellerSeat(null);
  }
}
