package com.nexage.app.dto.postauctiondiscount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(
    ignoreUnknown = true) // temp code as dealsEnabeld used by UI , TO stop breaking changes from UI
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAuctionDiscountDTO {
  @Schema(title = "A primary key for the table")
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Long pid;

  @Schema(title = "A unique name for the post auction discount")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String discountName;

  @Schema(
      title =
          "A flag to indicate whether this post auction discount is an include list (true) or an exclude list (false)")
  private Boolean discountStatus;

  @Schema(title = "The discount to apply to post auction discount")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private Double discountPercent;

  @Schema(title = "A description of this post auction discount")
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String discountDescription;

  @Schema(title = "A flag to indicate whether open auction is enabled")
  private Boolean openAuctionEnabled;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer version;

  @Schema(title = "A list of discount buyers for get by id data")
  private List<@Valid PostAuctionDiscountDspDTO> discountDSPs;

  @Schema(title = "A list of discount publishers for get by id data")
  private List<@Valid PostAuctionDiscountSellerDTO> discountSellers;

  @Schema(title = "A list of discount revenue groups for get by id data")
  private List<@Valid PostAuctionDiscountRevenueGroupDTO> discountRevenueGroups;

  @Schema(title = "A enum to indicate whether the ALL/NONE/SPECIFIC deals are selected")
  private PostAuctionDealsSelected dealsSelected;

  @Schema(title = "A list of discount deal Ids for get by id data")
  private List<DirectDealViewDTO> discountDeals;

  public PostAuctionDiscountDTO(
      Long pid,
      String discountName,
      Boolean discountStatus,
      Double discountPercent,
      String discountDescription) {
    this.pid = pid;
    this.discountName = discountName;
    this.discountStatus = discountStatus;
    this.discountPercent = discountPercent;
    this.discountDescription = discountDescription;
  }
}
