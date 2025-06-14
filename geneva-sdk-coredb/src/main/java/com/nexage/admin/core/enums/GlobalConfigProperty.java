package com.nexage.admin.core.enums;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum GlobalConfigProperty {
  ALLOW_REPORTING_API_PROPERTY("allow_reporting_api"),
  CALCULATE_DEPLOYABILITY_FOR_ALL_CAMPAIGNS_ON_STARTUP(
      "calculate_deployability_for_all_campaigns_on_startup"),
  ADSCREEN_IMAGE_BASE_URL("images_base_url"),
  ADSCREEN_BLOCKED_ADS_INTERVAL("blocked_ads_interval"),
  ADSCREEN_ALLOWED_ADS_INTERVAL("allowed_ads_interval"),
  ADSCREEN_REPORTING_ENABLED("adscreen_reporting_enabled"),
  BIDDER_HOST("bidder_host"),
  CREATIVE_CONFIG_DIR("bidder.creative.dir"),
  ADSCREEN_MAX_ADS_DISPLAY("screening_ads_max_limit"),
  ADSERVER_CREATIVE_DIR("adserver.creative.dir"),
  GENEVA_ERROR_TRACE_ENABLED("geneva.errortrace.enable"),
  GENEVA_ADSOURCES_WITH_ZERO_NEXAGE_REV_SHARE("geneva.adsources.with.zero.nexageRevshare"),
  GENEVA_CAMPAIGN_ADSERVER_BUYERPID("geneva.pss.autotag.generation.buyerpid"),
  SSO_B2B_TOKEN_REFRESH_INTERVAL("sso.b2b.token.refresh.interval"),
  REALM_NAME("realm.name"),
  SELLER_SITES_LIMIT("seller.sites.limit"),
  SELLER_POSITIONS_SITE_LIMIT("seller.positions.per.site.limit"),
  SELLER_TAGS_POSITION_LIMIT("seller.tags.per.position.limit"),
  SELLER_CAMPAIGNS_LIMIT("seller.campaigns.limit"),
  SELLER_CREATIVES_CAMPAIGN_LIMIT("seller.creatives.per.campaign.limit"),
  SELLER_BIDDER_LIBRARIES_LIMIT("seller.bidder.libraries.limit"),
  SELLER_BLOCK_LIBRARIES_LIMIT("seller.block.libraries.limit"),
  SELLER_USERS_LIMIT("seller.users.limit"),
  BUYER_LOGO_DIR("buyer.logo.dir"),
  BUYER_LOGO_BASE_URL("buyer.logo.base_url"),
  IMAGE_PROXY_ENABLED("image.proxy.enabled"),
  IMAGE_PROXY_URL("image.proxy.url"),
  BUYER_SEAT_EXISTENCE_VALIDATION_ENABLED("buyer.seat.existence.validation.enabled"),
  CKMS_ATHENS_CERT_PATH("ckms.athens.cert.path"),
  CKMS_ATHENS_KEY_PATH("ckms.athens.key.path"),
  CKMS_DEFAULT_KEYGROUPS_LIST("ckms.default.keygroups.list"),
  CKMS_YKEYKEY_ENVIRONMENT("ckms.ykeykey.environment"),
  CKMS_YKEYKEY_MOCK("ckms.ykeykey.mock"),
  CKMS_TRUST_STORE_PASSWORD_ENCODED("ckms.trust.store.password.encoded"),
  CRS_SSO_ENDPOINT("crs.sso.endpoint"),
  CRS_SSO_CLIENT_ID("crs.sso.clientId"),
  CRS_SSO_CLIENT_SECRET("crs.sso.secret"),
  CRS_SSO_KEY_GROUP("crs.sso.key.group"),
  CRS_SSO_REFRESH_FREQUENCY("crs.sso.refresh.period"),
  CRS_READ_API_ENDPOINT("crs.api.read.endpoint"),
  CRS_WRITE_API_ENDPOINT("crs.api.write.endpoint"),
  DEAL_ZERO_COST_SELLER_ALLOW_LIST("deal.guaranteed.zero_cost.seller.allow.list");

  private final String propertyName;

  GlobalConfigProperty(String propertyName) {
    this.propertyName = propertyName;
  }

  private static final Map<String, GlobalConfigProperty> map =
      Arrays.stream(values())
          .collect(toMap(GlobalConfigProperty::getPropertyName, Function.identity()));

  public static GlobalConfigProperty fromPropertyName(String propertyName) {
    return map.get(propertyName);
  }
}
