INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
values ('1', 1, 0, 'Deal Publisher', 'SELLER', 'http://test.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
values ('2', 2, 0, 'Pub 2', 'SELLER', 'http://test.com',
        'News Publisher', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO eligible_bidders (pid, TYPE, reference_id, bidders, VERSION)
VALUES (1, 'SELLER', 1, 0, 0),
       (2, 'SELLER', 2, 0, 0);

INSERT INTO site (company_pid, id, pid, dcn, name, domain, url, ad_screening, buyer_timeout,
                  consumer_profile_contributed,
                  consumer_profile_used, coppa_restricted, creation_date, days_free, filter_bots,
                  last_update, enable_groups, ad_truth, live, override_ip, platform,
                  report_batch_size, report_frequency,
                  rules_update_frequency, status, total_timeout, traffic_throttle, type,
                  version)
VALUES (1, '1', 1, 'dcn-1', 'site-1', 'site-1.com', 'http://site-1.com', 0, 0, 0, 0, 0,
        NOW(), 0, 0,
        NOW(), 0, 0, 0, 0, 'ANDROID', 0, 0, 0, 0, 0, 0, 'APPLICATION', 0);

INSERT INTO site (company_pid, id, pid, dcn, name, domain, url, ad_screening, buyer_timeout,
                  consumer_profile_contributed,
                  consumer_profile_used, coppa_restricted, creation_date, days_free, filter_bots,
                  last_update, enable_groups, ad_truth, live, override_ip, platform,
                  report_batch_size, report_frequency,
                  rules_update_frequency, status, total_timeout, traffic_throttle, type,
                  version)
VALUES (2, '10', 10, 'dcn-10', 'site-10', 'site-1.com', 'http://site-1.com', 0, 0, 0, 0, 0,
        NOW(), 0, 0,
        NOW(), 0, 0, 0, 0, 'ANDROID', 0, 0, 0, 0, 0, 0, 'APPLICATION', 0);

INSERT INTO position (site_pid, pid, name, mraid_support, mraid_adv_tracking, screen_location, video_support,
                      version,status)
VALUES (1, 1, 'pos-1', 0, 0, 0, 0, 0, 1);

INSERT INTO position (site_pid, pid, name, mraid_support, mraid_adv_tracking, screen_location, video_support,
                      version,status)
VALUES (10, 2, 'pos-2', 0, 0, 0, 0, 0, 1);

INSERT INTO deal (id, pid, description, version, creation_date, currency, priority_type, updated_on,
                  visibility, deal_category)
VALUES ('1', 1, 'Deal 1', 0, NOW(), 'EUR', 1, NOW(), 1, 1)
