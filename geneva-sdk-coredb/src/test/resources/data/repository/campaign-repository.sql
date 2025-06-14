INSERT INTO company (id, pid, version, name, type, url, description,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 300, 1, 'Company 1', 'SELLER', 'www.company1.com',
        'New Company', 0, 0, 0, 0, 0, 0);

INSERT INTO as_advertiser (pid, seller_id, name, status)
VALUES (200, 300, 'Advertiser 1', 0);

INSERT INTO as_campaign (pid, seller_id, advertiser_id, deployable, ext_id, lastupdate, model, name, price, start,
                         status, type, cap_hour, cap_24, cap_life)
VALUES (1, 1, 1, true, 1, '2020-12-12 13:12:11', 0, 'campagin_0', 100000, '2020-12-13 13:12:11', 0, 1, 100, 100, 100);
INSERT INTO as_campaign (pid, seller_id, advertiser_id, deployable, ext_id, lastupdate, model, name, price, start,
                         status, type, cap_hour, cap_24, cap_life)
VALUES (2, 1, 1, true, 1, '2020-12-12 13:12:11', 0, 'campagin_0', 100000, '2020-12-13 13:12:11', 4, 1, 100, 100, 100);
