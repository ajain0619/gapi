package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "as_advertiser")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Advertiser implements Serializable {

  private static final long serialVersionUID = 9143950402236202196L;

  public static final String HOUSE = "HOUSE";

  @AllArgsConstructor
  @Getter
  public enum AdvertiserStatus {
    INACTIVE(0),
    ACTIVE(1),
    DELETED(2);

    private final int externalValue;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "seller_id", nullable = false)
  @EqualsAndHashCode.Include
  private long sellerId;

  @Column(length = 100, nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private AdvertiserStatus status;

  public Advertiser(Long pid) {
    this.pid = pid;
  }

  public boolean isHouseAdvertiser() {
    return HOUSE.equals(name);
  }

  public static Advertiser createHouseAdvertiser(long sellerId) {
    Advertiser advertiser = new Advertiser();
    advertiser.setSellerId(sellerId);
    advertiser.setName(HOUSE);
    advertiser.setStatus(AdvertiserStatus.ACTIVE);
    return advertiser;
  }
}
