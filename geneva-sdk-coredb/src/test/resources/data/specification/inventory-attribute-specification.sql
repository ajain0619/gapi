INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb,
                     enable_mediation, rtb_revenue_report_enabled, status, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 10201, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb,
                     enable_mediation, rtb_revenue_report_enabled, status, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 72, 1, 'Nexage Inc 2', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

INSERT INTO attributes (pid, company_pid, status, last_update, has_global_visibility,
                        assigned_level,
                        is_required, is_internal, version, name)
VALUES (1, 10201, 1, '2021-07-01 10:10:10', 0, '1,2,3', 1, 1, 1, 'Attribute 1');
INSERT INTO attributes (pid, company_pid, status, last_update, has_global_visibility,
                        assigned_level,
                        is_required, is_internal, version, name)
VALUES (2, 10201, 1, '2021-07-01 10:10:10', 0, '3', 1, 1, 1, 'Attribute 2');
INSERT INTO attributes (pid, company_pid, status, last_update, has_global_visibility,
                        assigned_level,
                        is_required, is_internal, version, name)
VALUES (3, 10201, 1, '2021-07-01 10:10:10', 1, '3', 0, 1, 1, 'Attribute 3');
INSERT INTO attributes (pid, company_pid, status, last_update, has_global_visibility,
                        assigned_level,
                        is_required, is_internal, version, name)
VALUES (4, 10201, 1, '2021-07-01 10:10:10', 1, '1,2,3', 1, 1, 1, 'Attribute 4');
INSERT INTO attributes (pid, company_pid, status, last_update, has_global_visibility,
                        assigned_level,
                        is_required, is_internal, version, name)
VALUES (5, 10201, 1, '2021-07-01 10:10:10', 1, '2', 1, 1, 1, 'Attribute 5');


INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (1, 1, 10201, 0);

INSERT INTO attributes_company_visibility (pid, attribute_pid, company_pid, version)
VALUES (2, 2, 72, 0);
