USE `core`;

LOCK TABLES `company` WRITE;
INSERT INTO `company`
(`id`, `pid`, `description`, `name`, `type`, `url`, `allow_reporting_api`, `allow_ad_serving`, `restrict_drill_down`,
 `contact_id`, `VERSION`, `reporting_type`, `direct_ad_serving_fee`, `house_ad_serving_fee`, `non_remnant_house_ad_cap`,
 `house_ad_overage_fee`, `enable_cpi_tracking`, `cpi_conversion_notice_url`, `enable_rtb`, `enable_mediation`,
 `rtb_revenue_report_enabled`, `salesforce_id`, `adserving_fee`, `status`, `global_alias_name`, `test`,
 `selfserve_allowed`, `data_rights`, `payout_enabled`, `marketplace_id`, `third_party_fraud_detection_enabled`, `one_central_org_id`,
 `region_id`, `default_rtb_profiles_enabled`, `dynamic_buyer_registration_enabled`,
 `brxd_buyer_id_enabled_on_bid_request`, `dh_reporting_id`, `default_buyer_group`, `buyer_blocked_for_creative_scan`,
 `currency`)
VALUES ('2c988084016767c72b11c72d30000005', 10231, 'Provision company', 'NONRTBSELLER', 'SELLER',
        'http://www.seller.com', 1, 0, 0, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, NULL,
        NULL, 1, NULL, 0, 1, 0, 1, NULL, 0, NULL, NULL, 0, 0, 0, NULL,
        NULL, 0, default),('2c988084016767c72b11c742a020000d', 10233, '', '[Golden data] DSP', 'BUYER', 'www.buyer.com', 0, NULL, NULL,
        NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, NULL, NULL, 1, NULL, 0, 0, 0, 0,
        NULL, 0, NULL, NULL, 0, 0, 0, NULL, NULL, 0, default);
UNLOCK TABLES;

LOCK TABLES `site` WRITE;
INSERT INTO `site`
(`pid`, `ad_screening`, `consumer_profile_contributed`, `consumer_profile_used`, `creation_date`, `days_free`, `dcn`,
 `description`, `domain`, `ethnicity_map`, `filter_bots`, `gender_map`, `input_date_format`,
 `integration`, `last_update`, `live`, `marital_status_map`, `name`, `override_ip`, `platform`, `report_batch_size`,
 `report_frequency`, `revenue_launch_date`, `rules_update_frequency`, `send_ids`, `status`, `total_timeout`,
 `traffic_throttle`, `type`, `url`, `version`, `company_pid`, `buyer_timeout`, `id`, `app_bundle`,
 `enable_groups`, `coppa_restricted`, `rtb1_category_rollup`, `ad_truth`, `global_alias_name`,
 `mask_ip`, `metadata_enablement`, `hb_enabled`, `include_site_name`, `site_alias`, `site_name_alias`,
 `include_pub_name`, `pub_alias`, `pub_name_alias`, `rtb_profile`)
VALUES (10000208, 1, 0, 0, '2018-12-26 13:33:42', 0, '2c928084016767eb78e5ebcb35000005', NULL, 'seller.com',
        NULL, 1, NULL, NULL, 'SDK', '2018-12-26 13:34:05', 1, NULL, 'Site', 0, 'ANDROID_PHONE_TAB', 10,
        180000, '2018-12-26 13:33:42', 1800000, 1, 1, 5000, 0, 'APPLICATION',
        'play.google.com/store?id=com.test.android', 2, 10231, 500, '2c928084016767eb78e5ebcb35000006',
        'com.test.android', 0, 1, NULL, 0, NULL, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
UNLOCK TABLES;

LOCK TABLES `company` WRITE;
UPDATE company
set name = 'SMOKE TEST DO NOT TOUCH'
where name = '111 DJG Seller';
UNLOCK TABLES;

LOCK TABLES `deal_term` WRITE;
INSERT INTO `deal_term`
(`site_pid`, `effective_date`, `flat_bands`, `nexage_rev_share`, `revenue_mode`, `rtb_fee`, `tag_pid`, `pid`, `version`)
VALUES (10000208, '2019-01-07 17:04:33', NULL, 0.011, 'REV_SHARE', 0.00500000, NULL, 10964, 0);
UNLOCK TABLES;

LOCK TABLES `iab_cat` WRITE;
INSERT INTO `iab_cat`
    (`site_pid`, `category`)
VALUES (10000208, 'IAB1');
UNLOCK TABLES;

LOCK TABLES `seller_attributes` WRITE;
INSERT INTO `seller_attributes`
(`seller_pid`, `effective_date`, `rev_share`, `rtb_fee`, `version`, `default_block`, `default_bidder_groups`,
 `default_bidders_allowlist`, `hb_throttle`, `hb_throttle_perc`, `pfo_enabled`, `site_limit`,
 `positions_per_site_limit`, `tags_per_position_limit`, `campaigns_limit`, `creatives_per_campaign_limit`,
 `bidder_libraries_limit`, `block_libraries_limit`, `user_limit`, `limit_enabled`, `hb_price_preference`,
 `transparency_management_enablement`, `include_pub_name`, `pub_alias`, `pub_name_alias`, `super_auction_enabled`,
 `rtb_profile`, `crs_review_status_block`, `crs_secure_status_block`, `gdpr_jurisdiction`,
 `data_protection_first_third_party_distinction`, `second_price_premium_type`, `second_price_premium_flat`,
 `second_price_premium_percent`, `traffic_source_type`,
 `adfeedback_opt_out`, `revenue_group_pid`)
VALUES (10231, '2016-03-01 04:22:22', 0.01100000, 0.00500000, 0, '', '', 0, 0, 0, 0, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, '', 0, 0, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 1, 0, 0.01000000, 0.00, 0,
        0, 6),
       (10232, '2016-03-01 04:22:22', 0.01100000, 0.00500000, 0, '', '', 0, 0, 0, 0, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, '', 0, 0, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 1, 0, 0.01000000, 0.00, 0,
        0, 6);
ALTER TABLE seller_attributes MODIFY external_ad_verification_sampling_rate DECIMAL(5,2) DEFAULT NULL;
UNLOCK TABLES;

LOCK TABLES `seller_attributes_aud` WRITE;
ALTER TABLE seller_attributes_aud MODIFY external_ad_verification_sampling_rate DECIMAL(5,2) DEFAULT NULL;
UNLOCK TABLES;

LOCK TABLES `ad_source` WRITE;
UPDATE ad_source
SET name='Yahoo SSP'
WHERE pid = '4044';
UNLOCK TABLES;

/* Please add your user below if you want to have access to development environments. */
LOCK TABLES `app_user` WRITE;

INSERT INTO app_user (id, contact_number, creation_date, email, enabled, last_update, name, role, title,
                      user_name, VERSION, company_id, onecentral_username, first_name, last_name,  is_global)
VALUES ('ff8080810169695d89905d9414090006', null, '2019-03-14 13:37:35', 'onemobile.smoketests@gmail.com', true,
        '2019-03-14 13:38:02', 'xxxx', 'ROLE_ADMIN', null, 'onemobilesmokete401', 1, 1, 'onemobilesmokete401',
        'Onemobile', 'Smoketests', false);
UNLOCK TABLES;

LOCK TABLES `company_app_user` WRITE, `app_user` READ;
INSERT INTO company_app_user (user_id, company_id)
VALUES ((SELECT pid FROM app_user WHERE onecentral_username = 'onemobilesmokete401'), 1);

UNLOCK TABLES;

LOCK TABLES `app_user` WRITE;
INSERT INTO `app_user` (`id`, `pid`, `contact_number`, `creation_date`, `email`, `enabled`, `last_update`, `name`, `role`, `title`, `user_name`, `VERSION`, `company_id`, `onecentral_username`, `first_name`, `last_name`, `seller_seat_id`, `is_global`)
VALUES
  ('8a96dbb1016c6cfcdaf6fd9b974a0033', 3, NULL, '2019-09-04 14:48:48', 'svc-seller-ext-admin@geneva.com', 1, NULL, 'svc-seller-ext-admin', 'ROLE_ADMIN', 'svc-seller-ext-admin', 'svcsellerexta999', 0, 10232, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0044', 4, NULL, '2019-09-04 14:47:59', 'svc-seller-ext-manager@geneva.com', 1, NULL, 'svc-seller-ext-manager', 'ROLE_MANAGER', 'svc-seller-ext-manager', 'svcsellerextm664', 0, 10232, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0055', 5, NULL, '2019-09-04 13:20:06', 'svc-seller-ext-user@geneva.com', 1, NULL, 'svc-seller-ext-user', 'ROLE_USER', 'svc-seller-ext-user', 'svcsellerextu783', 0, 10232, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0066', 6, NULL, '2019-09-04 14:48:48', 'svc-seller-int-admin@geneva.com', 1, NULL, 'svc-seller-int-admin', 'ROLE_ADMIN', 'svc-seller-int-admin', 'svcsellerinta960', 0, 10231, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0077', 7, NULL, '2019-09-04 14:47:59', 'svc-seller-int-manager@geneva.com', 1, NULL, 'svc-seller-int-manager', 'ROLE_MANAGER', 'svc-seller-int-manager', 'svcsellerintm197', 0, 10231, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0088', 8, NULL, '2019-09-04 13:20:06', 'svc-seller-int-user@geneva.com', 1, NULL, 'svc-seller-int-user', 'ROLE_USER', 'svc-seller-int-user', 'svcsellerintu625', 0, 10231, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0099', 9, NULL, '2019-09-04 14:48:48', 'svc-buyer-ext-admin@geneva.com', 1, NULL, 'svc-buyer-ext-admin', 'ROLE_ADMIN', 'svc-buyer-ext-admin', 'svcbuyerextad957', 0, 10233, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0010', 10, NULL, '2019-09-04 14:47:59', 'svc-buyer-ext-manager@geneva.com', 1, NULL, 'svc-buyer-ext-manager', 'ROLE_MANAGER', 'svc-buyer-ext-manager', 'svcbuyerextma867', 0, 10233, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0011', 11, NULL, '2019-09-04 13:20:06', 'svc-buyer-ext-user@geneva.com', 1, NULL, 'svc-buyer-ext-user', 'ROLE_USER', 'svc-buyer-ext-user', 'svcbuyerextus158', 0, 10233, NULL, NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0012', 12, NULL, '2019-11-19 09:20:06', 'svc-seller-api-user@geneva.com', 1, NULL, 'svc-seller-api-user', 'ROLE_API', 'svc-seller-api-user', 'svcsellerapius', 0, 10201, 'svc-seller-api-user-1c', NULL, NULL, NULL, 0),
  ('8a96dbb1016c6cfcdaf6fd9b974a0013', 13, NULL, '2019-11-19 09:20:06', 'svc-buyer-api-user@geneva.com', 1, NULL, 'svc-buyer-api-user', 'ROLE_API', 'svc-buyer-api-user', 'svcbuyerapius', 0, 430, NULL, 'svc-buyer-api-user-1c', NULL, NULL, 0);

UNLOCK TABLES;

-- company_app_user table
LOCK TABLES `company_app_user` WRITE;
INSERT INTO `company_app_user` (`company_id`, `user_id`)
VALUES
  (10232, 3),
  (10232, 4),
  (10232, 5),
  (10231, 6),
  (10231, 7),
  (10231, 8),
  (10233, 9),
  (10233, 10),
  (10233, 11),
  (10201, 12),
  (430, 13);
UNLOCK TABLES;
UPDATE `global_config` SET `value` = 5 WHERE `property` = 'crs.sso.refresh.period';
UPDATE `global_config` SET `value` = 'http://127.0.0.1:8090' WHERE `property` = 'crs.sso.endpoint';
UPDATE `global_config` SET `value` = 'http://127.0.0.1:8090' WHERE `property` = 'crs.api.read.endpoint';

SET sql_mode=(SELECT REPLACE(@@GLOBAL.sql_mode, 'ONLY_FULL_GROUP_BY', ''));
SET sql_mode=(SELECT REPLACE(@@SESSION.sql_mode, 'ONLY_FULL_GROUP_BY', ''));

-- position and position_aud tables
LOCK TABLES `position` WRITE;
ALTER TABLE position MODIFY mraid_support SMALLINT DEFAULT NULL;
ALTER TABLE position MODIFY video_support TINYINT DEFAULT NULL;
ALTER TABLE position MODIFY screen_location TINYINT DEFAULT NULL;
ALTER TABLE position MODIFY external_ad_verification_sampling_rate DECIMAL(5,2) DEFAULT NULL;
UNLOCK TABLES;

LOCK TABLES `position_aud` WRITE;
ALTER TABLE position_aud MODIFY mraid_support SMALLINT DEFAULT NULL;
ALTER TABLE position_aud MODIFY video_support TINYINT DEFAULT NULL;
ALTER TABLE position_aud MODIFY screen_location TINYINT DEFAULT NULL;
ALTER TABLE position_aud MODIFY external_ad_verification_sampling_rate DECIMAL(5,2) DEFAULT NULL;
UNLOCK TABLES;
