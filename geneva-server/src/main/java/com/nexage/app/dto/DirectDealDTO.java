package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nexage.app.dto.deal.DealBidderDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.validator.deals.DirectDealCurrencyConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@DirectDealCurrencyConstraint
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@SuperBuilder
public class DirectDealDTO extends BaseDealDTO {

  @Schema(title = "Bid floor price")
  private BigDecimal floor;

  private Long createdBy;
  private AuctionType auctionType;
  @Valid private PlacementFormulaDTO placementFormula;
  private Boolean autoUpdate;
  private boolean allBidders;
  private boolean visibility;
  private boolean allSellers;
  private @Setter Integer dealCategory;
  @Default @Setter private List<DealBidderDTO> bidders = new ArrayList<>();
  @Default private List<RTBProfileDTO> profiles = new ArrayList<>();
  @Default private Set<DealRuleDTO> rules = new HashSet<>();
  @Default private List<DealPositionDTO> positions = new ArrayList<>();
  @Default private Set<DealTargetDTO> targets = new HashSet<>();
  @Default private List<DealSiteDTO> sites = new ArrayList<>();
  @Default @Setter private List<DealPublisherDTO> sellers = new ArrayList<>();
  @Setter private Long guaranteedImpressionGoal;
  @Setter private Long dailyImpressionCap;
  @Setter private Float viewability;
  @Setter private Boolean pacingEnabled;
  @Setter private Integer pacingStrategy;
  @Setter private boolean external;

  @Override
  @JsonProperty("dateCreated")
  public Date getCreationDate() {
    return super.getCreationDate();
  }

  /**
   * Used only to ensure the "publishers" field appears in the output JSON when this object is
   * marshaled (required by Cucumber tests).
   *
   * @deprecated Deprecated in favor of {@link #getSellers()} method
   * @return list of sellers
   */
  @Deprecated
  @JsonProperty("publishers")
  public List<DealPublisherDTO> getPublishers() {
    return getSellers();
  }

  public enum AuctionType {
    NONE(-1, "None"),
    FIRST_PRICE(1, "FirstPrice"),
    SECOND_PRICE_PLUS(2, "SecondPricePlus"),
    FIXED(3, "Fixed"), // shuffle bids
    FLOOR_PRICE(4, "FloorPrice"); // sort bids by price

    private final int value;
    private final String name;

    AuctionType(int value, String name) {
      this.value = value;
      this.name = name;
    }

    public int asInt() {
      return this.value;
    }

    public static AuctionType fromInt(int value) {
      return fromIntMap.get(value);
    }

    @JsonValue
    public String getName() {
      return name;
    }

    private static final HashMap<Integer, AuctionType> fromIntMap = new HashMap<>();

    static {
      for (AuctionType at : AuctionType.values()) {
        fromIntMap.put(at.value, at);
      }
    }
  }

  @Override
  public String toString() {
    return "DirectDealDTO{"
        + "pid="
        + getPid()
        + ", dealId='"
        + getDealId()
        + '\''
        + ", description='"
        + getDescription()
        + '\''
        + ", start="
        + getStart()
        + ", stop="
        + getStop()
        + ", floor="
        + floor
        + ", createdBy="
        + createdBy
        + ", status="
        + getStatus()
        + ", dateCreated="
        + getCreationDate()
        + ", auctionType="
        + auctionType
        + ", dealCategory="
        + dealCategory
        + ", bidders="
        + bidders
        + ", profiles="
        + profiles
        + ", rules="
        + rules
        + ", positions="
        + positions
        + ", visibility="
        + visibility
        + ", currency="
        + getCurrency()
        + ", targets="
        + targets
        + ", sites="
        + sites
        + ", sellers="
        + sellers
        + ", placementFormula='"
        + placementFormula
        + '\''
        + ", autoUpdate="
        + autoUpdate
        + ", guaranteedImpressionGoal="
        + guaranteedImpressionGoal
        + ", dailyImpressionCap="
        + dailyImpressionCap
        + ", viewability="
        + viewability
        + ", pacingEnabled="
        + pacingEnabled
        + ", pacingStrategy="
        + pacingStrategy
        + ", external="
        + external
        + '}';
  }
}
