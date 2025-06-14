insert into ad_source (pid, self_serve_enablement, name, status,
                       description, url_template, last_update, class_name, ad_type, creation_date, version, ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through, use_wrapped_sdk, use_device_useragent, id)
values (1, 0, 'D', 0,
        'description', 'url', '2013-04-18 10:44:35', 'class_name', 0, '2013-04-18 10:44:35', 0, 0, 0, 0, 0, 0, 0, 1, 'id123');
insert into ad_source (pid, self_serve_enablement, name, status,
                       description, url_template, last_update, class_name, ad_type, creation_date, version, ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through, use_wrapped_sdk, use_device_useragent, id)
values (2, 1, 'C', 0,
        'description', 'url', '2013-04-18 10:44:35', 'class_name', 0, '2013-04-18 10:44:35', 0, 0, 0, 0, 0, 0, 0, 1, 'id123');
insert into ad_source (pid, self_serve_enablement, name, status,
                       description, url_template, last_update, class_name, ad_type, creation_date, version, ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through, use_wrapped_sdk, use_device_useragent, id)
values (3, 0, 'B', -1,
        'description', 'url', '2013-04-18 10:44:35', 'class_name', 0, '2013-04-18 10:44:35', 0, 0, 0, 0, 0, 0, 0, 1, 'id123');
insert into ad_source (pid, self_serve_enablement, name, status,
                       description, url_template, last_update, class_name, ad_type, creation_date, version, ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through, use_wrapped_sdk, use_device_useragent, id)
values (4, 1, 'A', -1,
        'description', 'url', '2013-04-18 10:44:35', 'class_name', 0, '2013-04-18 10:44:35', 0, 0, 0, 0, 0, 0, 0, 1, 'id123');

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);
INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8sdfasd', 2, 1, 'Nexage Inc 2', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);

update ad_source set company_pid = 1 where pid in (1, 3);
update ad_source set company_pid = 2 where pid in (2, 4);
