package com.nexage.admin.core.dto;

import com.nexage.admin.core.bidder.type.BDRRule;
import com.nexage.admin.core.enums.site.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public final class PublisherFilterDTO {
  public enum FilterType {
    IAB_TYPE,
    IAB,
    TYPE;
  }

  private List<String> iabCat = new ArrayList<>();
  private BDRRule iabRule;
  private Type siteType;
  private BDRRule typeRule;
  private FilterType filterType;
}
