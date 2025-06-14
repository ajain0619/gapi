INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency, status, total_timeout, traffic_throttle, type,
                  url, version, company_pid, buyer_timeout, id, enable_groups,
                  coppa_restricted, ad_truth)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18', '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0',
        1, '1000', '4028811s1a242984011a74276dd12eee', 0, 0, 0);

INSERT INTO position (pid, name, site_pid, mraid_support, video_support, screen_location,
                      mraid_adv_tracking, version, status, position_alias_name, ad_size_type)
VALUES (1, 'footer', 1, 1, 1, 0, false, 1, 1, 'alias test1', 0);

INSERT INTO exchange_site_tag(pid, creation_date, tag_id, alter_reserve, version,
                              filter_bidders_whitelist, filter_bidders_allowlist, use_default_block,
                              use_default_bidders)
VALUES (1, '2019-01-02 10:00:00', 'primaryid', 1, 1, 0, 0, 0, 0);

INSERT INTO tag (pid, name, site_pid, status, primary_id, ecpm_provision, owner, version,
                 position_pid)
VALUES (1, 'sometag', 1, 1, 'primaryid', 'ecm', 1, 1, 1);
INSERT INTO tag (pid, name, site_pid, status, primary_id, ecpm_provision, owner, version,
                 position_pid)
VALUES (2, 'someothertag', 1, -1, 'primaryid', 'ecm', 1, 1, 1);

INSERT INTO tier (pid, tier_type, level, position_pid, order_strategy, VERSION)
VALUES (1, 0, 1, 1, 'Dynamic', 1);

INSERT INTO tier_tag (tag_pid, tier_pid, tag_order)
VALUES (1, 1, 1);

INSERT INTO rtb_profile_group (pid, name, VERSION, privilege_level, data_type, data, publisher_pid, list_type)
VALUES (1, 'rtbpgname', 1, 'NEXAGE_ONLY', 1, 'rtbpgdata', 1, 0);

INSERT INTO rtb_profile_library (pid, name, VERSION, privilege_level, publisher_pid,
                                 is_default_eligible)
VALUES (1, 'library', 1, 'NEXAGE_ONLY', 1, 0);

INSERT INTO rtb_profile_library_item (pid, library_pid, item_pid, VERSION)
VALUES (1, 1, 1, 1);

INSERT INTO rtb_profile_library_association (pid, rtb_profile_pid, library_pid, VERSION)
VALUES (1, 1, 1, 1);
