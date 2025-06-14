package com.nexage.app.dto.postauctiondiscount;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostAuctionDiscountIntermediaryDTO {
  private Long companyPid;
  private String companyName;
  private List<PostAuctionDiscountDspSeat> dspSeats;

  public PostAuctionDiscountIntermediaryDTO(List<PostAuctionDiscountDspSeat> dspSeats) {
    this.companyPid = dspSeats.get(0).getDsp().getCompany().getPid();
    this.companyName = dspSeats.get(0).getDsp().getCompany().getName();
    this.dspSeats = dspSeats;
  }
}
