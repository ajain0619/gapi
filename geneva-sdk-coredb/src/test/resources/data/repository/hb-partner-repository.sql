INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (37, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', 1, '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0);

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (45, 'position1', 37, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position1');;

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update,
                        formatted_default_type_enabled, multi_impression_bid, fill_max_duration)
VALUES (11, '5528811s1a242984011a74276dd12eee', 'Google', 'handler-one', 1, 1,
        '2019-07-20 12:25:18', false, true, true);

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update,
                        formatted_default_type_enabled, multi_impression_bid, max_ads_per_pod)
VALUES (12, '5528211s1a242984011a74276dd12eee', 'Amazon', 'handler-two', 1, 1,
        '2019-07-20 12:25:18', false, true, 8);

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update,
                        formatted_default_type_enabled)
VALUES (6, '5528211s1a242984011a74276dd12fff', 'Google EB', 'handler-three', 1, 1,
        '2019-07-20 12:25:18', true);

INSERT INTO hb_partner_company(pid, company_pid, external_pub_id, hb_partner_pid)
VALUES (1, 1, 'exId-1', 11);

INSERT INTO hb_partner_site(pid, site_pid, external_site_id, hb_partner_pid, type)
VALUES (1, 37, 'exId-2', 11, 1);

INSERT INTO hb_partner_position(pid, position_pid, external_position_id, hb_partner_pid, type)
VALUES (1, 45, 'exId-3', 12, 1);

INSERT INTO hb_partner_position(pid, position_pid, external_position_id, hb_partner_pid, type)
VALUES (2, 45, 'exId-4', 6, 2);
