ALTER TABLE company ALTER COLUMN pid BIGINT AUTO_INCREMENT;

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 300, 1, 'Bidder 1', 'BUYER', 'www.bidder1.com',
        'New Bidder', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO app_user (id, pid, version, creation_date, email, enabled, name,
                      role, user_name, company_id, is_global, onecentral_username)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 1, 1, '2011-01-01 00:00:00', 'superadmin@nexage.com',
        true, 'super admin', 'ROLE_ADMIN', 'superadmin', '1', 0, 'onecentral1');


INSERT INTO app_user (id, pid, version, contact_number, creation_date, email, enabled,
                      name, role, title, user_name, company_id, is_global, onecentral_username)
VALUES ('8a858acb012c2c608ee1608ee8cb3018', 2, 1, '202 890 0071', '2010-10-10 11:11:11',
        'inewton@test.com', true, 'Issac Newton', 'ROLE_MANAGER', 'Manager', 'inewton', '2', 0,
        'onecentral2');


INSERT INTO app_user (id, pid, version, contact_number, creation_date, email, enabled,
                      name, role, title, user_name, company_id, is_global, onecentral_username)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 3, 1, '781 890 0071', '2010-10-10 10:10:10',
        'jdeer@test.com', true, 'John Deer', 'ROLE_ADMIN', 'Supervisor', 'jdeer', '1', 0,
        'onecentral3');


INSERT INTO seller_seat (pid, name, status, version)
VALUES ('1', 'Test Seller Seat', 0, '1');


INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, seller_seat_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3018', 3, 1, 'Publisher With Seller Seat', 'SELLER',
        'www.pubwithsellerseat.com', 'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, '1', 0, 0, 0, 0);

INSERT INTO app_user (id, pid, version, contact_number, creation_date, email, enabled,
                      name, role, title, user_name, company_id, seller_seat_id, is_global,
                      onecentral_username)
VALUES ('8a858acb012c2c608ee1608ee8cb3018', 4, 1, '781 890 0072', '2019-08-04 10:10:10',
        'jtest@test.com', true, 'John Test', 'ROLE_ADMIN', 'Supervisor', 'jtest', '3', '1', 0,
        'onecentral4');


INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, seller_seat_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3019', 30, 1, 'API Role', 'SELLER', 'www.api_role.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, 0, 0, '1', 0, 0, 0, 0);

INSERT INTO app_user (id, pid, version, contact_number, creation_date, email, enabled,
                      name, role, title, user_name, company_id, seller_seat_id, is_global,
                      onecentral_username)
VALUES ('8b858acb012c2c608ee1608ee8cb3017', 40, 1, '781 890 0072', '2019-08-04 10:10:10',
        'api-test@test.com', false, 'API Test', 'ROLE_API', 'API', 'api-test', '30', '1', 0,
        'onecentral5');

INSERT INTO company_app_user (company_id, user_id)
VALUES ('1', '1'),
       ('2', '2'),
       ('1', '3'),
       ('1', '40');

UPDATE company
SET contact_id = (SELECT pid FROM app_user WHERE user_name = 'jdeer')
WHERE (pid = 1);

UPDATE company
SET contact_id = (SELECT pid FROM app_user WHERE user_name = 'inewton')
WHERE (pid = 2);

INSERT INTO user_restrictedsite (user_id, site_id)
VALUES (2, 1);
