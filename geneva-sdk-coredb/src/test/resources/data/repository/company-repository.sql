ALTER TABLE company ALTER COLUMN pid BIGINT AUTO_INCREMENT;

INSERT INTO seller_seat (pid, name, status, version)
VALUES (1, 'Test_Seller_Seat', 1, 1);

INSERT INTO seller_seat (pid, name, status, version)
VALUES (2, 'Test_Seller_Seat', 1, 1);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 2, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 3, 1, 'Seller 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3018', 4, 1, 'Test 4', 'SELLER', 'www.pub2.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3019', 5, 1, 'Test 5', 'BUYER', 'www.pub5.com',
        'News Publisher', 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608cb3020', 10, 1, 'Seatholder_1', 'SEATHOLDER', 'www.pub6.com',
        'News Publisher', 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 0, 0, 0);

INSERT INTO mdm_id (pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES (1, 'mdm1', null, 5, '2021-07-01 10:10:10', 'company');

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('1a158acb012c2c608ee1608ee8cb3020', 6, 1, 'Nexage Inc 6', 'SELLER', 'www.nexage6.com',
        'Mobile Ad Mediation 6', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId6', 1, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3020', 7, 1, 'Buyer 1', 'BUYER', 'www.buyer.com',
        'ABC Buyer', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 2, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3021', 8, 1, 'Buyer 2', 'BUYER', 'www.buyer2.com',
        'DEF Buyer', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 2, 0, 0, 0, 0);

INSERT INTO mdm_id (pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES (2, 'mdm2', null, 5, '2021-07-01 11:11:11', 'company');

INSERT INTO mdm_id (pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES (3, 'mdm2', null, 4, '2021-07-01 11:11:11', 'company');

INSERT INTO mdm_id (pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES (4, 'mdm2', null, 5, '2021-07-01 11:11:11', 'seller_seat');

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update)
VALUES ('1', '1testid', 'test_partner', 'test_handler', '1', '0', '2019-06-18 10:10:10');

INSERT INTO hb_partner (pid, id, name, partner_handler, status, version, last_update)
VALUES ('2', '2testid', 'test_partner2', 'test_handler2', '1', '0', '2019-06-18 11:11:11');

INSERT INTO hb_partner_company (pid, company_pid, external_pub_id, hb_partner_pid)
VALUES ('1', '1', 'test1', '1');

INSERT INTO hb_partner_company (pid, company_pid, external_pub_id, hb_partner_pid)
VALUES ('2', '1', 'test2', '2');


INSERT INTO seller_metrics (pid, company_pid, start_date, stop_date, ad_clicked, ad_requested,
                            ad_served, ad_delivered, fill_rate, ctr, rpm, ecpm, total_ecpm,
                            total_rpm, seller_revenue, total_revenue, verizon_revenue)
VALUES (1, 2, '2020-03-01 00:00:00', '2020-03-02 00:00:00', 4, 3, 2, 1, 0.5, 0.2, 0.1, 1.0, 2.0,
        3.0, 4.0, 10.0, 5.0);

INSERT INTO app_user (id, pid, version, contact_number, creation_date, email, enabled,
                      name, role, title, user_name, company_id, is_global)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 3, 1, '781 890 0071', '2010-10-10 10:10:10',
        'jdeer@test.com', true, 'John Deer', 'ROLE_ADMIN', 'Supervisor', 'jdeer', '6', '0');

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used,
                  creation_date, days_free, dcn, description, domain,
                  filter_bots, input_date_format, last_update, live, name, override_ip,
                  platform,
                  report_batch_size, report_frequency, revenue_launch_date, rules_update_frequency,
                  status, total_timeout, traffic_throttle, type, url, version,
                  company_pid, buyer_timeout, id, enable_groups, coppa_restricted, ad_truth, hb_enabled)
VALUES (1, 0, 1, 1, now(), 0, 'vmbc 1', 'Site description 1',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with name-1', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 1, 1000, 'id1', 0, 0, 0, 1),
       (2, 0, 1, 1, now(), 0, 'vmbc-2', 'Site description 2',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with name-2', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 5, 1000, 'id2', 0, 0, 0, 1),
       (3, 0, 1, 1, now(), 0, 'vmbc 3', 'Site description 3',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with text-3', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 6, 1000, 'id3', 0, 0, 0, 1),
       (4, 0, 1, 1, now(), 0, 'vmbc 4', 'Site description 4',
        'site.com', 1, 'yyyyMMdd', now(), 1, 'Site with text-4', 0, 'OTHER',
        180000, 180000, now(), 1800000, 1, 5000, 0, 'MOBILE_WEB',
        'ssfl', 0, 6, 1000, 'id4', 0, 0, 0, 1);

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, memo)
VALUES (1, 'P1', 1, 1, 1, 1, 1, 1, 1, 3, '200x300', 'P1');

INSERT INTO tag(pid, name, site_pid, status, primary_id, ecpm_provision, owner, version, position_pid)
VALUES(6, 'sometag6', 3, 1, 'primaryid', 'ecm', 1, 1, 1);

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (1, 6, 'test-1', 1, 'amazon.com', 'iab-cat');

INSERT INTO bdr_insertionorder (pid, ref_number, name, advertiser_pid, type, adomain, comments, bid_selector, iab_cat, updated_on, version)
VALUES (1, 'IO1', 'Insertion Order IO1', 1, 1, 'www.io1.com', 'this is a new insertion order', 0.1, 'IAB11-4', '2013-03-13 13:42:00', 0);

INSERT INTO bdr_lineitem (pid, name, version, deployable, freq_cap_mode, insertionorder_pid, status, start, type)
VALUES (2, 'lineItem', 0, true, 15, 1, 1, '1999-12-12 12:12:12', 0);

INSERT INTO bidder_config (id, name, VERSION, pid, traffic_status, format_type, bid_request_cpm,
                           include_block_lists, default_bid_currency, default_bid_unit,
                           bid_request_url, notice_url, filter_request_rate, filter_auction_types,
                           filter_countries, filter_categories,
                           filter_devices, filter_script, creation_date, last_update, company_id,
                           location_enabled_only, device_identified_only, ad_screening_enabled,
                           filter_ad_sizes)
VALUES ('BidderConfig-2', 'DEFAULTNAME', 1, 1, 1, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder1.com/getBid', 'http://bidder1.com/notifyWon', 0, 0, 'UNK,GNR', 'IBA1',
        null, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 5, 0, 0, 1,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768');

INSERT INTO external_data_provider (pid, name, base_url, enablement_status, last_update, description, data_provider_impl_class, filter_request_rate, VERSION, creation_date, bid_request_attr_name, bid_request_location, configuration, bidder_alias_required)
VALUES (1, 'ConnectPartner_1', 'https://gggoooo.com', 1, '2015-09-26 14:30:16', 'desc1', 'ic1', 0, 2, '2015-09-10 14:43:35', 'bra1', 1, 'config1', true);

INSERT INTO bidder_subscription(pid, data_provider_pid, bidder_id, bidder_pid, bidder_alias, requires_data_to_bid, version) VALUES (1, 1, 1, 1, 'ALIAS', true, 0);

insert into ad_source (pid, self_serve_enablement, name, status, company_pid,
                       description, url_template, last_update, class_name, ad_type, creation_date, version, ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through, use_wrapped_sdk, use_device_useragent, id)
values (1, 0, 'D', 1, 5,
        'description', 'url', '2013-04-18 10:44:35', 'class_name', 0, '2013-04-18 10:44:35', 0, 0, 0, 0, 0, 0, 0, 1, 'id123');

INSERT INTO app_user (id, pid, version, creation_date, email, enabled, name, role, user_name, company_id, is_global)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 1, 1, '2011-01-01 00:00:00', 'superadmin@nexage.com',
        true, 'super admin', 'ROLE_ADMIN', 'superadmin', 6, 0);

UPDATE company
SET contact_id = (SELECT pid FROM app_user WHERE user_name = 'jdeer')
WHERE pid = 1;
