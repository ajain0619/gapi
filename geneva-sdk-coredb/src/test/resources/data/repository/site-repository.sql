INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 11, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth, global_alias_name)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'dcn1', 'Site description with name-1', 'site1.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', 1, '5000', '0', 'MOBILE_WEB', 'url', '0', 11, '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0, 'global_alias_name1');
INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth, global_alias_name)
VALUES (2, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc2', 'Site description with name-2', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-2', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', -1, '5000', '0', 'MOBILE_WEB', 'ssfl', '0', 11, '1000',
        '4028811s1a242984011a74276dd12aaa', 0, 0, 0, 'global_alias_name2');

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update)
VALUES ('1', '1testid', 'test_partner', 'test_handler', '1', '0', '2019-06-18 10:10:10');

INSERT INTO hb_partner_company (pid, company_pid, external_pub_id, hb_partner_pid)
VALUES ('1', '11', 'externalPubId1', '1');

INSERT INTO hb_partner_site (pid, site_pid, external_site_id, hb_partner_pid)
VALUES ('1', '1', 'externalSiteId1', '1');

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth, global_alias_name)
VALUES (3, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbd', 'Site description with name-3', 'site3.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-3', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', 1, '5000', '0', 'MOBILE_WEB', 'ssfl2', '0', 11, '1000',
        '4028811s1a242984011a74276dd12eef', 0, 0, 0, 'global_alias_name3');

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (111, 3);

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (1, 'position1', 3, 1, 1, 1, 1, 1, 1, 3, '200x300', 200, 300, 'position1');
