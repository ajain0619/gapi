
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
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 3, 1, 'Publisher 2', 'SELLER', 'www.pub2.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (1, 1, 'test-1', 1, 'yahooinc1.com', 'iab-cat-1');

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (2, 2, 'test-2', 1, 'yahooinc2.com', 'iab-cat-2');

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (3, 3, 'test-3', 1, 'yahooinc3.com', 'iab-cat-3');

INSERT INTO bdr_insertionorder (pid, ref_number, name, advertiser_pid, type, adomain, comments, bid_selector, iab_cat, updated_on, version)
VALUES (1, 'IO1', 'Insertion Order IO1', 1, 1, 'www.io1.com', 'this is a new insertion order', 0.1, 'IAB11-4', '2013-03-13 13:42:00', 0);

INSERT INTO bdr_insertionorder (pid, ref_number, name, advertiser_pid, type, adomain, comments, bid_selector, iab_cat, updated_on, version)
VALUES (2, 'IO2', 'Insertion Order IO2', 1, 1, 'www.io2.com', 'this is a new insertion order', 0.2, 'IAB11-4', '2013-03-13 13:42:00', 0);

INSERT INTO bdr_insertionorder (pid, ref_number, name, advertiser_pid, type, adomain, comments, bid_selector, iab_cat, updated_on, version)
VALUES (3, 'IO3', 'Insertion Order IO3', 2, 1, 'www.io3.com', 'this is a new insertion order', 0.3, 'IAB11-4', '2013-03-13 13:42:00', 0);

INSERT INTO bdr_insertionorder (pid, ref_number, name, advertiser_pid, type, adomain, comments, bid_selector, iab_cat, updated_on, version)
VALUES (4, 'IO4', 'Insertion Order IO4', 3, 1, 'www.io4.com', 'this is a new insertion order', 0.4, 'IAB11-4', '2013-03-13 13:42:00', 0);
