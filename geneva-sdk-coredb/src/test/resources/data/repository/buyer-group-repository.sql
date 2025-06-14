INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Nexage Inc2', 'NEXAGE', 'www.nexage2.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (1, 'buyer_group_1', 1, 'sfdc_line_id_1', 'sfdc_io_id_1', 'USD', 'USA', true, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (2, 'buyer_group_2', 1, 'sfdc_line_id_2', 'sfdc_io_id_2', 'USD', 'USA', true, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (3, 'buyer_group_3', 1, 'sfdc_line_id_3', 'sfdc_io_id_3', 'EUR', 'DEU', true, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (4, 'buyer_group_4', 2, 'sfdc_line_id_4', 'sfdc_io_id_4', 'EUR', 'DEU', true, 0);
