INSERT INTO seller_seat (pid, name, status, version)
VALUES (1, 'Test_Seller_Seat', 1, 1);

INSERT INTO seller_seat (pid, name, status, version)
VALUES (2, 'Test_Seller_Seat', 1, 1);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO bdr_exchange (pid, ext_id, name, handler, status, version, updated_on, nexage_id, bidding_fee, bidder_site, booking_event, tmax, tmargin)
VALUES (1, '821fa20b006347f29025e2fd47e1da11', 'Nexage Exchang 1', 'NEXAGE', 1, 1, '2014-04-16 17:45:54', 2160, 0.00, 1, 0, 0, 0);

INSERT INTO bdr_exchange (pid, ext_id, name, handler, status, version, updated_on, nexage_id, bidding_fee, bidder_site, booking_event, tmax, tmargin)
VALUES (2, '821fa20b006347f29025e2fd47e1da12', 'Nexage Exchang 2', 'NEXAGE', 1, 1, '2014-04-16 17:45:54', 2160, 0.00, 1, 0, 0, 0);

INSERT INTO bdr_exchange (pid, ext_id, name, handler, status, version, updated_on, nexage_id, bidding_fee, bidder_site, booking_event, tmax, tmargin)
VALUES (3, '821fa20b006347f29025e2fd47e1da13', 'Nexage Exchang 3', 'NEXAGE', 1, 1, '2014-04-16 17:45:54', 2160, 0.00, 1, 0, 0, 0);

INSERT INTO bdr_exchange_company (exchange_pid, company_pid, bidding_fee, version, updated_on)
VALUES (1, 1, 0.11, 0, '2019-06-18 10:10:10');

INSERT INTO bdr_exchange_company (exchange_pid, company_pid, bidding_fee, version, updated_on)
VALUES (2, 1, 0.12, 0, '2019-06-18 10:10:11');

INSERT INTO bdr_exchange_company (exchange_pid, company_pid, bidding_fee, version, updated_on)
VALUES (3, 1, 0.13, 0, '2019-06-18 10:10:12');

INSERT INTO bdr_exchange_company (exchange_pid, company_pid, bidding_fee, version, updated_on)
VALUES (3, 2, 0.15, 0, '2019-06-18 10:10:14');
