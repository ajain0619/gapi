INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO app_user (id, pid, version, creation_date, email, enabled, name, role, user_name,
                      company_id, is_global)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 1, 1, '2011-01-01 00:00:00', 'superadmin@nexage.com',
        true, 'super admin', 'ROLE_ADMIN', 'superadmin', '1', 0);

INSERT INTO company_app_user (company_id, user_id)
VALUES ('1', '1');

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 2, 1, 'Nexage Inc2', 'NEXAGE', 'www.nexage2.com',
        'Mobile Ad Mediation2', 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO app_user (id, pid, version, creation_date, email, enabled, name, role, user_name,
                      company_id, is_global)
VALUES ('8a858acb012c2c608ee1608ee8cb3018', 2, 1, '2011-01-01 00:00:00', 'superadmin2@nexage.com',
        true, 'super admin2', 'ROLE_ADMIN', 'superadmin2', '1', 0);

INSERT INTO company_app_user (company_id, user_id)
VALUES ('2', '2');

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (11, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '1', '1000',
        '4028811s1a242984011a74276dd12eee', 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (22, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc2', 'Site description with name-2',
        'site2.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-2', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '1', '1000',
        '2', 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (33, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc3', 'Site description with name-3',
        'site3.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-3', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '1', '1000',
        '3', 0, 0, 0);

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (1, 11);

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (2, 11);

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (2, 22);

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (2, 33);
