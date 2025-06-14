INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 300, 1, 'BUYER-1', 'BUYER', 'www.buyer1`.com',
        'New Bidder', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb4018', 301, 0, 'BUYER-2', 'BUYER', 'www.buyer2.com',
        'New Bidder', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO bidder_config (id, name, VERSION, pid, traffic_status, format_type, bid_request_cpm,
                           include_block_lists, default_bid_currency, default_bid_unit,
                           bid_request_url, notice_url, filter_request_rate, filter_auction_types,
                           filter_countries, filter_countries_allowlist, filter_categories,
                           filter_categories_allowlist, filter_devices, filter_devices_allowlist,
                           filter_script, creation_date, last_update, company_id,
                           location_enabled_only, device_identified_only, ad_screening_enabled,
                           filter_sites_allowlist, filter_publishers_allowlist, filter_ad_sizes,
                           filter_ad_sizes_allowlist)
VALUES ('BidderConfig-1', 'DEFAULTNAME', 1, 3001, 1, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder1.com/getBid', 'http://bidder1.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 300, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false);

INSERT INTO bidder_config (id, name, VERSION, pid, traffic_status, format_type, bid_request_cpm,
                           include_block_lists, default_bid_currency, default_bid_unit,
                           bid_request_url, notice_url, filter_request_rate, filter_auction_types,
                           filter_countries, filter_countries_allowlist, filter_categories,
                           filter_categories_allowlist, filter_devices, filter_devices_allowlist,
                           filter_script, creation_date, last_update, company_id,
                           location_enabled_only, device_identified_only, ad_screening_enabled,
                           filter_sites_allowlist, filter_publishers_allowlist, filter_ad_sizes,
                           filter_ad_sizes_allowlist)
VALUES ('BidderConfig-2', 'DEFAULTNAME', 1, 3002, 1, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder1.com/getBid', 'http://bidder1.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 300, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false);

INSERT INTO bidder_config (id, name, VERSION, pid, traffic_status, format_type, bid_request_cpm,
                           include_block_lists, default_bid_currency, default_bid_unit,
                           bid_request_url, notice_url, filter_request_rate, filter_auction_types,
                           filter_countries, filter_countries_allowlist, filter_categories,
                           filter_categories_allowlist, filter_devices, filter_devices_allowlist,
                           filter_script, creation_date, last_update, company_id,
                           location_enabled_only, device_identified_only, ad_screening_enabled,
                           filter_sites_allowlist, filter_publishers_allowlist, filter_ad_sizes,
                           filter_ad_sizes_allowlist)
VALUES ('BidderConfig-3', 'DEFAULTNAME', 1, 3003, 0, 'OpenRTBv2', 0.0005, 1, 'USD', 0,
        'http://bidder1.com/getBid', 'http://bidder1.com/notifyWon', 0, 0, 'UNK,GNR', 0, 'IBA1', 0,
        null, 0, 0, '2011-02-17 00:00:00', '2011-02-17 00:00:00', 300, 0, 0, 1, true, true,
        '320x50,300x250,320x480,728x90,480x320,768x1024,1024x768', false);