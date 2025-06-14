INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 1, 0, 0, 'dhReportingId', 0, 0, 0, 0),
        ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Nexage2 Inc', 'NEXAGE2', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 1, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO exchange_site_tag (tag_id, VERSION, pid, creation_date, last_update, site_alias,
                               site_name_alias, site_type, pub_alias, pub_name_alias,
                               include_site_name, include_consumer_id, include_consumer_profile,
                               include_domain_references, default_reserve, auction_type,
                               blocked_ad_types, blocked_ad_categories, blocked_advertisers,
                               description, filter_bidders, filter_bidders_whitelist, filter_bidders_allowlist,
                               screening_level, blocked_external_data_providers, include_geo_data,
                               site_pid, blocked_attributes, low_reserve, pub_net_reserve,
                               pub_net_low_reserve, use_default_block, use_default_bidders,
                               alter_reserve, tag_pid, include_pub_name, name,
                               default_rtb_profile_owner_company_pid, status)

VALUES ('4028ea11016e6e88cebc8a3015000005', 0, 60000, '2019-11-20 14:00:37', '2019-11-20 14:00:37',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S11', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'My Default RTB Profile S11',
        1, 1),
       ('4028ea11016e6e88cebc8a7e2c5f0008', 0, 60001, '2019-11-20 15:25:55', '2019-11-20 15:25:55',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S21', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'My Default RTB Profile S21',
        1, 1),
       ('4028ea11016e6e88cebc8a8935fb000b', 0, 60002, '2019-11-20 15:37:58', '2019-11-20 15:37:58',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S31', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'My Default RTB Profile S31',
        1, 1),
       ('4028ea11016e6e88cebc8a8a3b7e000e', 0, 60003, '2019-11-20 15:39:05', '2019-11-20 15:39:05',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S12', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'My Default RTB Profile S12',
        1, 1),
       ('4028ea11016e6e88cebc8a8b9a7d0011', 0, 60004, '2019-11-20 15:40:35', '2019-11-20 15:40:35',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S22', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'My Default RTB Profile S22',
        1, 1),
       ('4028ea11016e6e88cebc8a8c9bc40014', 0, 60005, '2019-11-20 15:41:41', '2019-11-20 15:41:41',
        NULL, NULL, '\0', NULL, NULL, NULL, 1, 1, 1, 0.00000000, 0, NULL, NULL, NULL,
        'Self Service Created Default RTB Profile S32', NULL, NULL, NULL, 0, NULL, 1, NULL, NULL,
        0.00000000, 10.00000000, NULL, 1, 1, 1, NULL, NULL, 'No related name',
        1, 1);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth, rtb_profile)
VALUES (1, 2, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '1', '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0, null),
        (2, 2, 1, 1, '2018-06-10 12:25:18', '0', 'vmbd', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-2', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '2', '1000',
        '4028811s1a242984011a74276dd12eef', 0, 0, 0, null),
        (3, 2, 1, 1, '2018-06-10 12:25:18', '0', 'vmbe', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-3', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '2', '1000',
        '4028811s1a242984011a74276dd12eff', 0, 0, 0, 60002);

INSERT INTO seller_attributes (seller_pid, version, data_protection_first_third_party_distinction,
                               default_bidders_allowlist, hb_throttle,
                               hb_price_preference, transparency_management_enablement,
                               super_auction_enabled, limit_enabled,
                               pfo_enabled,rtb_profile)
VALUES ('1', 0, 0, true, true, 10, 10, true, true, true,60000);

INSERT INTO seller_attributes (seller_pid, version, data_protection_first_third_party_distinction,
                               default_bidders_allowlist, hb_throttle,
                               hb_price_preference, transparency_management_enablement,
                               super_auction_enabled, limit_enabled,
                               pfo_enabled,rtb_profile)
VALUES ('2', 0, 1, true, true, 10, 10, true, true, true,60001);

INSERT INTO seller_attributes (seller_pid, version, data_protection_first_third_party_distinction,
                               default_bidders_allowlist, hb_throttle,
                               hb_price_preference, transparency_management_enablement,
                               super_auction_enabled, limit_enabled,
                               pfo_enabled,rtb_profile)
VALUES ('3', 0, 2, true, true, 10, 10, true, true, true,60002);

INSERT INTO position(pid, name, site_pid, mraid_support,video_support,screen_location, mraid_adv_tracking, version, status, position_alias_name, ad_size_type, rtb_profile, memo, placement_type) VALUES(1,'footer',1,1,1,0,false,1,1, 'alias test1', 0, 60000, 'test_memo', 0);
INSERT INTO position(pid, name, site_pid, mraid_support,video_support,screen_location, mraid_adv_tracking, version, status, position_alias_name, ad_size_type, rtb_profile, memo, placement_type) VALUES(2,'header',1,1,2,0,false,1,1, 'alias test2', 0, 60000, 'test_memo', 0);
INSERT INTO position(pid, name, site_pid, mraid_support,video_support,screen_location, mraid_adv_tracking, version, status, position_alias_name, ad_size_type, rtb_profile, memo, placement_type) VALUES(3,'middle',2,1,2,0,false,1,1, 'alias test3', 0, null, 'test_memo2', 3);
INSERT INTO position(pid, name, site_pid, mraid_support,video_support,screen_location, mraid_adv_tracking, version, status, position_alias_name, ad_size_type, rtb_profile, memo, placement_type) VALUES(4,'left',3,1,2,0,false,1,1, 'alias test4', 0, null, 'test_memo2', 3);
INSERT INTO position(pid, name, site_pid, mraid_support,video_support,screen_location, mraid_adv_tracking, version, status, position_alias_name, ad_size_type, rtb_profile, memo, placement_type) VALUES(5,'deleted',3,1,2,0,false,1,-1, 'alias test5', 0, null, 'test_memo3', 8);

