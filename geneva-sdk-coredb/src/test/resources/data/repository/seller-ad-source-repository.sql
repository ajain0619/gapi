INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id, disable_ad_feedback,
                     external_ad_verification_enabled, third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES
('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com', 'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId1', 0, 0, 0, 0),
('8a858acb012c2c608ee1608ee8cb3016', 2, 1, 'Company 1', 'SELLER', 'www.company1.com', 'New Company', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId2', 0, 0, 0, 0);


INSERT INTO ad_source(pid, ad_type, class_name, creation_date, description, last_update, name, url_template, version, company_pid,
                      ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through,
                      id, self_serve_enablement, status, use_wrapped_sdk, use_device_useragent)
VALUES
(1, 1, 'classname', '2019-01-01 10:11:12', 'description', '2019-02-01 10:00:00', 'name', 'url template', 1, 1, 0, 0, 1, 0, 0, '123455', 1, 1, 1, false),
(2, 1, 'classname', '2019-01-01 10:11:12', 'description', '2019-02-01 10:00:00', 'name', 'url template', 1, 1, 0, 0, 1, 0, 0, '123455', 1, 1, 1, false),
(3, 1, 'classname', '2019-01-01 10:11:12', 'description', '2019-02-01 10:00:00', 'name', 'url template', 1, 1, 0, 0, 1, 0, 0, '123455', 1, 1, 1, false);

INSERT INTO seller_adsource(
 pid, seller_pid, adsource_pid, adnetreport_username, adnetreport_password,
  adnetreport_apitoken, adnetreport_apikey, version)
VALUES
(1, 1, 1, 'username', 'password', 'apitoken', 'apikey', 0),
(2, 2, 1, 'username', 'password', 'apitoken', 'apikey', 0),
(3, 1, 2, 'username', 'password', 'apitoken', 'apikey', 0);
