INSERT into exchange_regional(pid, id)
VALUES (1, 'USA');

INSERT into company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 300, 1, 'Bidder 1', 'BUYER', 'www.bidder1.com',
        'New Bidder', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
       ('8a858acb012c2c608ee1608ee8cb3018', 301, 1, 'Bidder 2', 'BUYER', 'www.bidder2.com',
        'New Bidder 2', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO bidder_config(id, name, VERSION, pid, traffic_status, format_type, bid_request_cpm,
                          include_block_lists, default_bid_currency, default_bid_unit,
                          bid_request_url, notice_url, filter_request_rate, filter_auction_types,
                          filter_countries, filter_countries_allowlist, filter_categories,
                          filter_categories_allowlist, filter_devices,
                          filter_devices_allowlist,
                          filter_script, creation_date, last_update, company_id,
                          location_enabled_only, device_identified_only, ad_screening_enabled,
                          filter_sites_allowlist,filter_publishers_allowlist, filter_ad_sizes,
                          filter_ad_sizes_allowlist, billing_src, allow_bridgeId_match, allow_connect_id,
                          allow_id_graph_match, allow_liveramp, domain_verification_auth_level, send_deal_sizes, app_bundle_filter_allow_list, app_bundle_filter_allow_unknown_apps)
VALUES ('BidderConfig-1', 'DEFAULTNAME', 1, 100, 1, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder1.com/getBid', 'http://bidder1.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 300, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false, 1, true, true, true, true, 0, true, false, true),
       ('BidderConfig-2', 'DEFAULTNAME', 1, 101, 1, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder2.com/getBid', 'http://bidder2.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 300, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false, 2, false, false, false, false, 1, false, false, true),
       ('BidderConfig-3', 'DEFAULTNAME', 1, 102, 0, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder3.com/getBid', 'http://bidder3.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 301, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false, null, false, false, false, false, 2, false, false, true),
        ('BidderConfig-4', 'DEFAULTNAME', 1, 103, 0, 'OpenRTBv2_5_1', 0.0005, 1, 'USD', 0,
        'http://bidder3.com/getBid', 'http://bidder3.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 301, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false, null, false, true, false, false, 2, false, false, true);

INSERT into buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable)
VALUES (1, 'buyer_group_1', 300, 'sfdc_line_id_1', 'sfdc_io_id_1', 'USD', 'USA', true);

INSERT into buyer_seat (pid, name, seat, buyer_group_pid, company_pid, enabled, creation_date,
                        last_updated_date)
VALUES (1, 'seat1_name', 'seat1', 1, 300, true, now(), now());

INSERT INTO device_type (pid, id, name) VALUES (1, 2, 'Personal Computer'), (2, 3, 'Connected TV'), (3, 4, 'Phone'), (4, 6, 'Set Top Box');


INSERT INTO bidder_config_device_type (bidder_pid, device_type_id, version) VALUES (100, 2, 0), (100, 4, 0);

INSERT INTO identity_provider (pid, version, name, display_name, provider_id, domain, enabled, ui_visible)
VALUES (1, 1, 'CONNECTID', 'connectId', -1, 'yahoo.com, verizonmedia.com', true, false),
       (2, 1, 'LIVERAMP', 'RampId', 0, 'liveramp.com', true, true),
       (3, 1, 'MICROSOFT', 'muId', 1, 'microsoft.com', true, true),
       (4, 1, 'YAHOO_COOKIE', 'yahooCookie', 2, 'yahoo_cookie', true, true);

INSERT INTO bidder_identity_provider VALUES (100, 3), (100, 4);
