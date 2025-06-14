INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'SELLER Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Acme Inc', 'SELLER', 'www.acme.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReporting_Id', 0, 0, 0, 0);


INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (2, 2, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com',
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
VALUES (3, 3, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc1', 'Site description with name-1', 'site1.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-2', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfe', '0', '1', '1000',
        '4028811s1a242984011a74276dd12ee1', 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform, report_batch_size, report_frequency, revenue_launch_date,
                  rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted,
                  ad_truth)
VALUES (4, 4, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc2', 'Site description with name-3', 'site1.com',
        1, 'yyyyMMdd', '2013-04-18 10:44:35', 1, 'Site with name-2', 0, 'OTHER', '180000',
        '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfe', '0', '2', '1000',
        '4028811s1a242984011a742y6dd12ee1', 0, 0, 0);



INSERT INTO position(pid, name,site_pid, status, mraid_support,video_support,screen_location, mraid_adv_tracking, version) VALUES(2,'footer',2,1,1,1,0,false,1);
INSERT INTO position(pid, name,site_pid, status, mraid_support,video_support,screen_location, mraid_adv_tracking, version) VALUES(3,'header',2,1,1,1,0,false,1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (1, 0, 5, 1, 'deal', 2, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, NULL, 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (2, 0, 6, 1, 'Test-deal', 2, '2020-06-10 13:10:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', false, 3, 'formula 1', 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (111, 0, '5550', 1, 'deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, 'formula 2', 0);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (110, 0, '5551', 1, 'deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, 'formula 3', 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (112, 0, 'deal-id', '456546', 'x-deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 200,
        '2020-06-20 10:00:00', true, 2, 'formula 3', 1);

INSERT INTO deal_rule(pid, deal_pid, rule_group_pid, version)
values (1, 1, 1, 0),
       (2, 2, 2, 0);


INSERT INTO deal_site(pid, deal_pid, site_pid, version ) VALUES (1, 1, 2,0);
INSERT INTO deal_site(pid, deal_pid, site_pid, version ) VALUES (2, 1, 3,0);
INSERT INTO deal_site(pid, deal_pid, site_pid, version ) VALUES (3, 2, 2,0);



INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (1, 1, 2,0);
INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (2, 1, 3,0);
INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (3, 2, 2,0);

INSERT INTO deal_publisher(pid, deal_pid, pub_pid, version ) VALUES (1, 1, 1,0);
INSERT INTO deal_publisher(pid, deal_pid, pub_pid, version ) VALUES (2, 2, 2,0);



INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type)
VALUES (1, 1, 1, 1, 'Test Rule One', 'Test Rule', now(), 1),
       (2, 2, 1, -1, 'Test Rule Two', 'Test Rule', now(), 1),
       (3, 1, 1, -1, 'Test Rule three', 'Test Rule', now(), 1);

INSERT INTO rule_target(pid, version, status, match_type, target_type, data, rule_pid)
VALUES (1, 0, 1, 1, 22, '[{"buyerCompany":10205}]', 1),
       (2, 0, 1, 0, 22, '[{"buyerCompany":10205,bidders:[8]},{"buyerCompany":10208}]', 2),
       (3, 0, 1, 1, 22, '[{"buyerCompany":123, seats:["12","34","22"]},{"buyerCompany":300}]', 3);


