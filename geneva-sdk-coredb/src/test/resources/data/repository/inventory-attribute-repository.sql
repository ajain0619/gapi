-- companies
INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb,
                     enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,
                     default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3017', 10232, 1, '[Golden Data] RTB Seller', 'SELLER',
        'www.Seller.com',
        'New Seller', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
values ('8a858acb012c2c608ee1608ee8cb3016', 10233, 1, 'Publisher 1', 'SELLER', 'www.pub1.com',
        'News Publisher', 0, 0, 0, 0, 0, 0, false, false, false, 0, 0, false, false);

-- inventory attributes
INSERT INTO attributes (pid, company_pid, description, status, last_update, assigned_level,
                        has_global_visibility, is_internal, is_required, version, name, prefix)
VALUES (1, 10232, 'description 1', 1, '2019-02-08 00:00:00', '1', 1, 1, 1, 0, 'Inventory Attribute 1', 'pref'),
       (2, 10232, NULL, 1, '2019-02-08 00:00:00', '1,2,3', 1, 1, 1, 0, 'Inventory Attribute 2', 'pref'),
       (3, 10232, NULL, 1, '2019-02-08 00:00:00', '2', 1, 1, 1, 0, 'Inventory Attribute3', 'pref'),
       (4, 10232, 'description 4', 1, '2018-06-10 12:25:18', '1,2,3', 0, 1, 1, 1, 'Inventory Attribute 4', 'TestPrefix'),
       (5, 10232, 'description 5', 1, '2018-06-10 12:25:18', '1,2', 0, 1, 1, 1, 'Inventory Attribute 51', NULL),
       (6, 10233, 'description 5', 1, '2018-06-10 12:25:18', '1,2', 0, 1, 1, 1, 'Inventory Attribute 61', NULL),
       (7, 10233, NULL, -1, '2018-06-10 12:25:18', '1,2', 0, 1, 1, 1, 'Inventory Attribute 7', NULL),
       (8, 10233, NULL, 0, '2018-06-10 12:25:18', '1,2', 0, 1, 1, 1, 'Inventory Attribute 8', NULL);

-- inventory attribute values
INSERT INTO attribute_values (pid, name, is_enabled, last_update, attribute_pid, version)
VALUES (5, 'at1value1', true, '2018-06-10 12:25:18', 4, 1);

INSERT INTO attribute_values (pid, name, is_enabled, last_update, attribute_pid, version)
VALUES (6, 'at1value12', false, '2018-06-10 12:25:18', 4, 1);
