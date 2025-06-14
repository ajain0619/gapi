INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (12345, 'Foo', 'Bar', true, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 300, 1, 'Test 1', 'SELLER', 'www.test.com',
        'New Test Company', 0, 0, 0, 0, 0, 0, 0, 0, 0, 12345, 0, 0, 0, 0);

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (2, 'Foo', 'Bar', true, 0);

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (3, 'Foo', 'Bar', false, 0);

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (4, 'Testseat', 'Testseat', false, 0);

INSERT INTO seller_seat (pid, name, description, status, version)
VALUES (5, 'Testseat2', 'Testseat2', false, 0);

INSERT INTO mdm_id (pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES (1, 'mdm2', 12345, null, '2021-07-01 11:11:11', 'seller_seat');
