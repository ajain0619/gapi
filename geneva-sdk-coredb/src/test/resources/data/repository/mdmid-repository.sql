
INSERT INTO seller_seat (pid, name, status, version)
VALUES (1, 'Test_Seller_Seat_1', 1, 1);

INSERT INTO seller_seat (pid, name, status, version)
VALUES (2, 'Test_Seller_Seat_2', 1, 1);

INSERT INTO company(id, pid, version, name, type, url, description, enable_cpi_tracking,
                    enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                    selfserve_allowed, default_rtb_profiles_enabled,
                    dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                    seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                    third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
       'News Publisher', 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO company(id, pid, version, name, type, url, description, enable_cpi_tracking,
                    enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                    selfserve_allowed, default_rtb_profiles_enabled,
                    dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                    seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                    third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES('8a858acb012c2c608ee1608ee8cb3016', 3, 1, 'Publisher 2', 'SELLER', 'www.pub2.com',
       'News Publisher', 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0);

INSERT INTO company(id, pid, version, name, type, url, description, enable_cpi_tracking,
                    enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                    selfserve_allowed, default_rtb_profiles_enabled,
                    dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                    seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                    third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES('8a858acb012c2c608ee1608ee8cb3016', 4, 1, 'Publisher 3', 'SELLER', 'www.pub2.com',
       'News Publisher', 0, 1, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0);

INSERT INTO mdm_id(pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES(1, 'mdm1', null, 2, '2022-05-01 10:10:10', 'company');
INSERT INTO mdm_id(pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES(2, 'mdm2', null, 2, '2022-05-01 10:10:10', 'company');
INSERT INTO mdm_id(pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES(3, 'mdm3', 1, null, '2022-05-01 10:10:10', 'seller_seat');
INSERT INTO mdm_id(pid, id, seller_seat_pid, company_pid, last_update, type)
VALUES(4, 'mdm4', null, 4, '2022-05-01 10:10:10', 'company');
