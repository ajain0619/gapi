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
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (1, 1, 'test-1', 1, 'yahooinc.com', 'iab-cat');

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (2, 2, 'test-1', 1, 'yahooinc.com', 'iab-cat-2');

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (3, 1, 'test-3', 1, 'yahooinc.com', 'iab-cat-2');
