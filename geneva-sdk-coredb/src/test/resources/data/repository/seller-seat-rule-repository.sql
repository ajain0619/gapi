INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (1, 'Camelot', 'Castle where the Knights of the Round Table live', true, 1);

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (2, 'Aaaaaargh', 'Castle where the Holy Grail is', true, 1);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, seller_seat_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 1, 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform,
                  report_batch_size, report_frequency, revenue_launch_date, rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted, ad_truth)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1',
        'site.com', 1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-1', 0,
        'OTHER',
        '180000', '180000', '2018-06-10 12:25:18', '1800000', '1', '5000', '0', 'MOBILE_WEB',
        'ssfl', '0', '1', '1000', '4028811s1a242984011a74276dd12eee', 0, 0, 0);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (1, NULL, 1, 1, 'Lancelot 123', 'Brave knight', now(), 1, 1);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (2, NULL, 1, 1, 'Robin 456', 'Not-Quite-so-Brave-as-Sir-Lancelot knight', now(), 1, 1);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (3, NULL, 1, 1, 'Rude soldier', 'Insults the knights', now(), 1, 2);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (4, 1, 1, 1, 'Tim', 'Enchanter', now(), 1, NULL);

INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type,
                  seller_seat_pid)
VALUES (5, NULL, 1, -1, 'Seller', 'Seat deleted rule', now(), 1, 2);
