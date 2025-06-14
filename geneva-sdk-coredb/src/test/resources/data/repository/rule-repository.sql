-- adding seller_seat

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (1, 'seat name', 'seat description', true, 1);

-- adding company

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
values ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
values ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
values ('8a858acb012c2c608ee1608ee8cb3037', 3, 1, 'Publisher 2', 'SELLER', 'www.pub2.com',
        'News Publisher2', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, seller_seat_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('id1', 4, 1, 'Nexage Inc 3', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation 3', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 1, 0, 0, 0, 0),
       ('id2', 5, 1, 'Nexage Inc 2  ', 'NEXAGE', 'www.nexage2.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 1, 0, 0, 0, 0);

-- adding site

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency, status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted, ad_truth)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Test Site For Rule', 'site.com', 1,
        'yyyyMMdd', '2013-04-18 10:44:35', 1, 'me.com', 0, 'OTHER', '180000', '180000',
        '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '2', '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0),
       (2, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbcabcd', 'Test Site For Rule', 'site2.com', 1,
        'yyyyMMdd', '2013-04-18 10:44:35', 1, 'me.sss.com', 0, 'OTHER', '180000', '180000',
        '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '3', '1000',
        '4028811s1a242984011a74276dd12aaa', 0, 0, 0),
       (3, 0, 1, 1, now(), 0, 'vmbc 1', 'Site description 1',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with name-1', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 4, 1000, 'id1', 0, 0, 0),
       (4, 0, 1, 1, now(), 0, 'vmbc-2', 'Site description 2',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with name-2', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 4, 1000, 'id2', 0, 0, 0),
       (5, 0, 1, 1, now(), 0, 'vmbc 3', 'Site description 3',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with name-3', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 4, 1000, 'id3', 0, 0, 0),
       (6, false, false, false, now(), 0, 'id6', null,
        'android_phonetab_site.test', true, null, now(), true, 'android_phonetab_site', false, 'ANDROID_PHONE_TAB',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'https://play.google.com/store/apps/details?id=android_phonetab_site', 0, 3, 500, 'id6', false, false, false),
       (7, false, false, false, now(), 0, 'id7', null,
        'android_site.test', true, null, now(), true, 'android_site', false, 'ANDROID',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'https://play.google.com/store/apps/details?id=android_site', 0, 3, 500, 'id7', false, false, false),
       (8, false, false, false, now(), 0, 'id8', null,
        'android_tab.test', true, null, now(), true, 'android_tab_site', false, 'ANDROID_TAB',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'https://play.google.com/store/apps/details?id=android_tab_site', 0, 3, 500, 'id8', false, false, false),
       (9, false, false, false, now(), 0, 'id9', null,
        'ipad_iphone_site.test', true, null, now(), true, 'ipad_iphone_site', false, 'IPAD_IPHONE',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'http://itunes.apple.com/ipad_iphone_site', 0, 3, 500, 'id9', false, false, false),
       (10, false, false, false, now(), 0, 'id10', null,
        'iphone_site.test', true, null, now(), true, 'iphone_site', false, 'IPHONE',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'http://itunes.apple.com/iphone_site', 0, 3, 500, 'id10', false, false, false),
       (11, false, false, false, now(), 0, 'id11', null,
        'ipad_site.test', true, null, now(), true, 'ipad_site', false, 'IPAD',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'http://itunes.apple.com/ipad_site', 0, 3, 500, 'id11', false, false, false),
       (12, false, false, false, now(), 0, 'id12', null,
        'ctv_site.test', true, null, now(), true, 'ctv_site', false, 'CTV_OTT',
        10, 180000, now(), 1800000, 1, 5000, 0, 'APPLICATION',
        'http://ctv_site.test', 0, 3, 500, 'id12', false, false, false);

-- adding iab_cat

INSERT INTO iab_cat (site_pid, category)
VALUES (1, 'IAB1'),
       (1, 'IAB2');

-- adding position

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (1, 'position1', 1, 1, 1, 1, 1, 1, 1, 3, '200x300', 200, 300, 'position1');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (2, 'position2', 1, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position2');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (3, 'position3', 2, 1, 1, 1, 1, 1, 1, 3, '200x300', 200, 300, 'position3');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (4, 'position4', 2, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position4');

INSERT INTO position (pid, name, site_pid, is_default, is_interstitial,
                      version, mraid_support, video_support, screen_location, mraid_adv_tracking,
                      ad_size,
                      static_ad_unit, rich_media_ad_unit, rm_mraid_version, video_mraid_2,
                      video_proprietary,
                      video_vast, video_response_protocol, video_playback_method, video_start_delay,
                      fullscreen_timing, position_alias_name, memo, updated_on,
                      video_linearity,
                      video_maxdur, height, width, video_skippable, video_skipthreshold,
                      video_skipoffset,
                      status, placement_type, traffic_type, rtb_profile, ad_size_type,
                      native_config)
VALUES (11, 'header', 3, 0, 0, 1, 1, 0, -1, 1, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (12, 'footer', 3, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (13, 'header 2', 4, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (14, 'footer 2', 5, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (15, 'android_phonetab_site_banner', 6, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (16, 'android_site_banner', 7, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (17, 'android_tab_site_banner', 8, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (18, 'ipad_iphone_site_banner', 9, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (19, 'iphone_site_banner', 10, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (20, 'ipad_site_banner', 11, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL),
       (21, 'ctv_site_banner', 12, 0, 0, 1, 1, 0, -1, 1, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL);

-- adding placement_video

INSERT INTO placement_video (pid, version, vast_version, vpaid_support, wrapper_support, failover_support,
                             file_formats, player_required, linearity, longform)
VALUES (1, 1, '2', 0, 1, 0, '0', 0, 1, 1);

INSERT INTO placement_video (pid, version, vast_version, vpaid_support, wrapper_support, failover_support,
                             file_formats, player_required, linearity, longform)
VALUES (2, 1, '2', 0, 1, 0, '0', 0, 1, 0);

-- adding rule

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type)
VALUES (1, 1, 1, 1, 'Test Rule Two', 'Test Rule', now(), 1);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type)
VALUES (2, 1, 1, -1, 'Test Rule Three', 'Test Rule', now(), 1);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (3, 4, 1, 1, 'rule name 3', 'rule description 3', now(), 1, NULL),
       (4, 4, 1, 1, 'rule name 4', 'rule description 4', now(), 1, NULL),
       (5, NULL, 1, 1, 'rule name 5', 'rule description 5', now(), 1, 1),
       (6, 4, 1, -1, 'rule name 6', 'rule description 6', now(), 1, NULL),
       (7, 4, 1, 1, 'rule name 7', 'rule description 7', now(), 1, NULL),
       (8, 5, 1, 1, 'rule test 8', 'rule description 8', now(), 1, NULL),
       (9, 5, 1, 1, 'rule test 9', 'rule description 9', now(), 1, NULL),
       (10, 5, 1, 1, 'rule name 10', 'rule description 10', now(), 1, NULL),
       (11, 5, 1, 1, 'rule test 11', 'rule description 11', now(), 1, NULL),
       (13, 4, 1, 0, 'rule name 13', 'rule description 13', now(), 1, NULL);

-- adding rule_target

INSERT INTO rule_target (pid, version, status, match_type, target_type, data, rule_pid)
VALUES (1, 1, 1, 1, 1, 'Test Data One', 1);

INSERT INTO rule_target (pid, version, status, match_type, target_type, data, rule_pid)
VALUES (2, 1, 1, 1, 1, 'Test Data One', 2);

-- adding rule_formula

INSERT INTO rule_formula (pid, rule_pid, last_update, auto_update, version, formula)
VALUES (1, 1, now(), 1, 0,'{
  "groupedBy": "OR",
  "formulaGroups": [
    {
      "formulaRules": [
        {
          "attribute": "SITE_NAME",
          "operator": "EQUALS",
          "ruleData": "me.com"
        },
        {
          "attribute": "PLACEMENT_NAME",
          "operator": "CONTAINS",
          "ruleData": "1"
        }
      ],
      "inventoryAttributes": [
        [
          {
            "attribute": "INVENTORY_ATTRIBUTE_PID",
            "operator": "EQUALS",
            "ruleData": "1"
          },
          {
            "attribute": "INVENTORY_ATTRIBUTE_VALUE",
            "operator": "EQUALS",
            "ruleData": "English"
          }
        ]
      ]
    },
    {
      "formulaRules": [
        {
          "attribute": "SITE_NAME",
          "operator": "NOT_CONTAINS",
          "ruleData": "sdf"
        },
        {
          "attribute": "PLACEMENT_NAME",
          "operator": "CONTAINS",
          "ruleData": "2"
        }
      ]
    },
    {
      "formulaRules": [
        {
          "attribute": "SITE_IAB_CATEGORY",
          "operator": "CONTAINS",
          "ruleData": "IAB1,IAB2"
        }
      ]
    }
  ]
}
');

INSERT INTO rule_formula (pid, rule_pid, last_update, auto_update, version, formula)
VALUES (2, 2, now(), 1, 0,'{
  "groupedBy": "OR",
  "formulaGroups": [
    {
      "formulaRules": [
        {
          "attribute": "SITE_NAME",
          "operator": "EQUALS",
          "ruleData": "me.com"
        },
        {
          "attribute": "PLACEMENT_NAME",
          "operator": "CONTAINS",
          "ruleData": "1"
        }
      ],
      "inventoryAttributes": [
        [
          {
            "attribute": "INVENTORY_ATTRIBUTE_PID",
            "operator": "EQUALS",
            "ruleData": "1"
          },
          {
            "attribute": "INVENTORY_ATTRIBUTE_VALUE",
            "operator": "EQUALS",
            "ruleData": "English"
          }
        ]
      ]
    },
    {
      "formulaRules": [
        {
          "attribute": "SITE_NAME",
          "operator": "NOT_CONTAINS",
          "ruleData": "sdf"
        },
        {
          "attribute": "PLACEMENT_NAME",
          "operator": "CONTAINS",
          "ruleData": "2"
        }
      ]
    },
    {
      "formulaRules": [
        {
          "attribute": "SITE_IAB_CATEGORY",
          "operator": "CONTAINS",
          "ruleData": "IAB1,IAB2"
        }
      ]
    }
  ]
}
');

-- adding site_rule

INSERT INTO site_rule (site_pid, rule_pid)
VALUES (3, 3),
       (3, 4),
       (4, 7),
       (5, 3);

-- adding position_rule

INSERT INTO position_rule (position_pid, rule_pid)
VALUES (11, 3),
       (12, 3),
       (13, 5),
       (14, 7),
       (11, 9);

-- adding rule

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type)
VALUES (12, 1, 1, 1, 'Test Deal Rule', 'Test Deal Rule', now(), 2);

-- adding company_rule

INSERT INTO company_rule (rule_pid, company_pid)
VALUES (1, 4),
       (2, 4),
       (4, 5),
       (6, 5),
       (7, 5),
       (12, 5);


-- adding deal

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency, all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula, auto_update)
VALUES (1, 0, 5, 1, 'deal', 2, '2021-08-30 13:00:00', 'USD', false, false, 100, '2021-08-30 10:00:00', true, 1, NULL, 1);

-- adding deal_rule

INSERT INTO deal_rule(pid, deal_pid, rule_group_pid, version)
values (1, 1, 12, 0);
