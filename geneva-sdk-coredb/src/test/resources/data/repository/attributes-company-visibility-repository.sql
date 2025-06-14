INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3019', 3, 1, 'Publisher 2', 'SELLER', 'www.pub2.com',
        'News Publisher2', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency, status, total_timeout, traffic_throttle, type,
                  url, version, company_pid, buyer_timeout, id, enable_groups, coppa_restricted, ad_truth)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Test Site For Rule', 'site.com', 1,
        'yyyyMMdd', '2013-04-18 10:44:35', 1, 'me.com', 0, 'OTHER', '180000', '180000',
        '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '2', '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0);

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version)
VALUES (1, 'position1', 1, 1, 1, 1, 1, 1, 1);

INSERT INTO attributes (pid, company_pid, name, prefix, description, status, last_update,
                        has_global_visibility, assigned_level, is_required, is_internal, version)
VALUES (1, 1, 'testa1', 'TestPrefix', 'testa1', 1, '2018-06-10 12:25:18', 1, '1,2,3', 1, 1, 1);

INSERT INTO attributes (pid, company_pid, name, prefix, description, status, last_update,
                        has_global_visibility, assigned_level, is_required, is_internal, version)
VALUES (2, 2, 'testa2', 'TestPrefix', 'testa2', 0, '2018-06-10 12:25:18', 1, '1', 1, 1, 1);

INSERT INTO attributes (pid, company_pid, name, prefix, description, status, last_update,
                        has_global_visibility, assigned_level, is_required, is_internal, version)
VALUES (3, 3, 'testa3', 'TestPrefix', 'testa3', 1, '2018-06-10 12:25:18', 0, '2', 1, 1, 1);

INSERT INTO attributes (pid, company_pid, name, prefix, description, status, last_update,
                        has_global_visibility, assigned_level, is_required, is_internal, version)
VALUES (4, 1, 'testa23', '', 'testa23', 1, '2018-06-10 12:25:18', 0, '1,2', 1, 1, 1);

INSERT INTO attribute_values (pid, name, is_enabled, last_update, attribute_pid, version)
VALUES (1, 'at1value1', 1, '2018-06-10 12:25:18', 1, 1);

INSERT INTO attribute_values (pid, name, is_enabled, last_update, attribute_pid, version)
VALUES (2, 'at1value12', 0, '2018-06-10 12:25:18', 1, 1);

INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (1, 1, 1, 1);

INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (2, 2, 2, 1);

INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (3, 3, 3, 1);

INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (4, 4, 1, 1);
