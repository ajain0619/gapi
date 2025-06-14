package com.nexage.admin.core.enums;

import static com.nexage.admin.core.enums.RuleTargetCategory.BID;
import static com.nexage.admin.core.enums.RuleTargetCategory.BUYER;
import static com.nexage.admin.core.enums.RuleTargetCategory.SUPPLY;

import java.util.HashMap;
import java.util.regex.Pattern;

public enum RuleTargetType implements HasInt<RuleTargetType> {
  COUNTRY(SUPPLY, 1) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.CountryValidator} */
    }
  },
  REGION(SUPPLY, 2) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  STATE(SUPPLY, 3) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  CITY(SUPPLY, 4) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  GENDER(SUPPLY, 9) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  AGE(SUPPLY, 10) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.AgeTargetValidator} */
    }
  },
  AD_SIZE(SUPPLY, 13) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.AdSizeTargetValidator} */
    }
  },
  EXACT_MATCH_ADVERTISER_DOMAIN(BID, 15) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.AdvertiserDomainTargetValidator} */
    }
  },
  IAB_CATEGORY(BID, 16) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  CREATIVE_ID(BID, 18) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  MATCHED_USER_ID(BUYER, 21) {
    @Override
    public void validateTargetData(String targetData) {
      // TODO: 10/08/2018
    }
  },
  BUYER_SEATS(BUYER, 22) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.BuyerSeatTargetValidator} */
    }
  },
  BIDDER(BUYER, 26) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.BidderTargetValidator} */
    }
  },
  CREATIVE_BEACON(BID, 24) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.CreativeBeaconValidator} */
    }
  },
  WILDCARD_ADVERTISER_DOMAIN(BID, 25) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.AdvertiserDomainTargetValidator} */
    }
  },
  BUCKET(SUPPLY, 27) {
    @Override
    public void validateTargetData(String targetData) {}
  },
  CREATIVE_LANGUAGE(BID, 28) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.CreativeLanguageValidator} */
    }
  },
  MULTI_AD_SIZE(BUYER, 29) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.services.validation.sellingrule.AdSizeTargetValidator} */
    }
  },
  DEVICE_TYPE(SUPPLY, 33) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_CHANNEL(SUPPLY, 37) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_SERIES(SUPPLY, 38) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_RATING(SUPPLY, 39) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_GENRE(SUPPLY, 40) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  AD_FORMAT_TYPE(BUYER, 41) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  PUBLISHER(SUPPLY, 42) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RulePublisherValidation} */
    }
  },
  REVGROUP(SUPPLY, 43) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleRevGroupValidation} */
    }
  },
  VIDEO_COMPLETION_RATE(SUPPLY, 44) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  PLAYLIST_RENDERING_CAPABILITY(SUPPLY, 46) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_LIVESTREAM(SUPPLY, 49) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  CONTENT_LANGUAGE(SUPPLY, 50) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  },
  DEAL_CATEGORY(BUYER, 51) {
    @Override
    public void validateTargetData(String targetData) {
      /* {@link com.nexage.app.util.validator.RuleTargetValidator} */
    }
  };

  private RuleTargetCategory category;
  private int type;

  RuleTargetType(RuleTargetCategory category, int type) {
    this.category = category;
    this.type = type;
  }

  public RuleTargetCategory getCategory() {
    return category;
  }

  public int asInt() {
    return type;
  }

  public RuleTargetType fromInt(int i) {
    return fromIntMap.get(i);
  }

  private static final Pattern TRAFFIC_TYPE_DATA_PATTERN = Pattern.compile("\\s*,\\s*");

  private static final HashMap<Integer, RuleTargetType> fromIntMap = new HashMap<>();

  static {
    for (RuleTargetType s : RuleTargetType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public abstract void validateTargetData(String targetData);
}
