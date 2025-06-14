INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('company_1_id', 1, 1, 'company_1', 'NEXAGE', 'company_1_url',
        'company_1_description', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
       ('company_2_id', 2, 1, 'company_2', 'NEXAGE', 'company_2_url',
        'company_2_description', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);


INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (1, 'buyer_group_1', 1, 'sfdc_line_id_1', 'sfdc_io_id_1', 'USD', 'USA', true, 0);

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (1, 1, 1, 'name_1', 'seat_1', false, 0, now(), now()),
       (2, 1, 1, 'name_2', 'seat_2', false, 0, now(), now()),
       (3, 2, 1, 'name_3', 'seat_3', false, 0, now(), now()),
       (4, 2, 1, 'name_4', 'seat_4', false, 0, now(), now());
