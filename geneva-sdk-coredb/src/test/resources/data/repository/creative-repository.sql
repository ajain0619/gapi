INSERT INTO company (id, pid, version, name, type, url, description,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 300, 1, 'Company 1', 'BUYER', 'www.company1.com',
        'New Company', 0, 0, 0, 0, 0, 0);

INSERT INTO as_advertiser (pid, seller_id, name, status)
VALUES (200, 300, 'Advertiser 1', 0);

INSERT INTO as_campaign (pid, seller_id, advertiser_id, ext_id, name, type, model, price, goal,
                         daily, cap_hour, cap_24, cap_life, bias, start, stop, status, lastUpdate,
                         deployable)
VALUES (400, 300, 200, 444, 'Campaign 1', 0, 0, 0.0, 0, 0, 0, 0, 0, 0.0, '2012-03-27 12:35:00.0',
        '2012-03-27 12:35:00.0', 0, '2012-03-27 12:35:00.0', 0);

INSERT INTO as_creative (pid, seller_id, advertiser_id, name, ad_type, banner, mma_120x20,
                         mma_168x28, mma_216x36, mma_300x50, mma_320x50, banner_alt, ad_text,
                         landing_url, tracking_url, lastUpdate, status, height, width, template_id,
                         custom_markup)
VALUES (1, 300, 200, 'Creative 1', 0, '', '', '', '', '', '', '', '', '', '',
        '2012-03-27 12:35:00.0', 0, 100, 100, 0, '');
INSERT INTO as_creative (pid, seller_id, advertiser_id, name, ad_type, banner, mma_120x20,
                         mma_168x28, mma_216x36, mma_300x50, mma_320x50, banner_alt, ad_text,
                         landing_url, tracking_url, lastUpdate, status, height, width, template_id,
                         custom_markup)
VALUES (2, 300, 200, 'Creative 2', 0, '', '', '', '', '', '', '', '', '', '',
        '2012-03-27 12:35:00.0', 1, 100, 100, 0, '');

INSERT INTO as_campaign_creative (seller_id, campaign_id, advertiser_id, markup, lastUpdate,
                                  creative_id)
VALUES (300, 400, 200, '', '2012-03-27 12:35:00.0', 1);
INSERT INTO as_campaign_creative (seller_id, campaign_id, advertiser_id, markup, lastUpdate,
                                  creative_id)
VALUES (300, 400, 200, '', '2012-03-27 12:35:00.0', 2);
