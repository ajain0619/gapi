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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "bidder_private_attribute")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BidderPrivateAttribute implements Serializable {

  private static final long serialVersionUID = 6750033365092817367L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "attribute_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  /** the outbound key in the bid request */
  @Column(name = "bid_request_attr_name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String bidRequestAttributeName;

  @Column(name = "description")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String description;

  /** true if private attribute is for subscription data */
  @Column(name = "is_subscription_data")
  @ToString.Include
  private boolean subscriptionData;

  /**
   * source key of data in inbound request (if isSubscriptionData is false) or source key of data in
   * subscription data service return (if isSubscriptionData is true)
   */
  @Column(name = "inbound_attr_name")
  @ToString.Include
  private String inboundAttributeName;

  /** Where in the bidRequest to place the outbound private attribute */
  @Column(name = "bid_request_location")
  @Enumerated(EnumType.ORDINAL)
  @ToString.Include
  private BidRequestLocation bidRequestLocation;
}
