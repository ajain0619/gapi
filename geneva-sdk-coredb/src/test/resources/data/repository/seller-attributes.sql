INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb,
                     enable_mediation, rtb_revenue_report_enabled, status, selfserve_allowed,
                     default_rtb_profiles_enabled, dynamic_buyer_registration_enabled,
                     brxd_buyer_id_enabled_on_bid_request, dh_reporting_id,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled,
                     fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 666, 1, 'SellerCompany', 'SELLER', 'www.whatever.com',
        'SellerCompany', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 0, 0, 0, 0);
INSERT INTO seller_attributes (seller_pid, version, data_protection_first_third_party_distinction,
                               default_bidders_allowlist, hb_throttle,
                               hb_price_preference, transparency_management_enablement,
                               super_auction_enabled, limit_enabled,
                               pfo_enabled, smart_qps_enabled,
                               external_ad_verification_sampling_rate, enable_ctv_selling, creative_success_rate_threshold,
                               creative_success_rate_threshold_opt_out, raw_response)
VALUES ('1', 0, 0, true, true, 10, 10, true, true, true, false, 30.5, false, 1.00, false, true);
