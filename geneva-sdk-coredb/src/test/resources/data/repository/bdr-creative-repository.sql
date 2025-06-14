INSERT INTO seller_seat (pid, name, status, version)
VALUES (1, 'Test_Seller_Seat', 1, 1);

INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking,
                     enable_rtb, enable_mediation, rtb_revenue_report_enabled, status,
                     selfserve_allowed, default_rtb_profiles_enabled,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     dh_reporting_id, seller_seat_id, disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 0, 0, 0, 0, 'dhReportingId', 1, 0, 0, 0, 0);

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (1, 1, 'BDRAdvertiser0', 1, 'yahooinc.com', 'iab-cat');

INSERT INTO bdr_advertiser (pid, company_pid, name, status, adomain, iab_cat)
VALUES (2, 1, 'BDRAdvertiser1', 1, 'yahooinc.com', 'iab-cat');

INSERT INTO bdr_creative (
                          pid,
                          name,
                          status,
                          banner,
                          width,
                          height,
                          custom_markup,
                          landing_url,
                          tracking_url,
                          indicative_url,
                          advertiser_pid,
                          version
                          ) VALUES (
                                1,
                                'BDRCreative0',
                                1,
                                'http://banner.BDRCreative0',
                                600,
                                800,
                                '<div>BDRCreative0</div>',
                                'http://landing.BDRCreative0',
                                'http://tracking.BDRCreative0',
                                'http://indicative.BDRCreative0',
                                1,
                                1);

INSERT INTO bdr_creative (
    name,
    status,
    banner,
    width,
    height,
    custom_markup,
    landing_url,
    tracking_url,
    indicative_url,
    version
) VALUES (
             'BDRCreative1',
             0,
             'http://banner.BDRCreative1',
             600,
             800,
             '<div>BDRCreative1</div>',
             'http://landing.BDRCreative1',
             'http://tracking.BDRCreative1',
             'http://indicative.BDRCreative1',
             1);

INSERT INTO bdr_creative (
    name,
    status,
    banner,
    width,
    height,
    custom_markup,
    landing_url,
    tracking_url,
    indicative_url,
    version
) VALUES (
             'BDRCreative2',
             1,
             'http://banner.BDRCreative2',
             600,
             800,
             '<div>BDRCreative2</div>',
             'http://landing.BDRCreative2',
             'http://tracking.BDRCreative2',
             'http://indicative.BDRCreative2',
             1);

INSERT INTO bdr_creative (
    name,
    status,
    banner,
    width,
    height,
    custom_markup,
    landing_url,
    tracking_url,
    indicative_url,
    version
) VALUES (
             'BDRCreative3',
            1,
             'http://banner.BDRCreative3',
             600,
             800,
             '<div>BDRCreative3</div>',
             'http://landing.BDRCreative3',
             'http://tracking.BDRCreative3',
             'http://indicative.BDRCreative3',
             1);

