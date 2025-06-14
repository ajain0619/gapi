INSERT INTO company (id                             , pid , version, name           , type    , url                    , description    , enable_cpi_tracking, enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed, default_rtb_profiles_enabled, dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback, external_ad_verification_enabled, third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES              ('fee-adjustment-test-company-1', 1111, 1      , 'Test Buyer 1' , 'BUYER' , 'www.test-buyer-1.com' , 'a test buyer' , 0                  , 0         , 0               , 0                         , 0                , 1                        , 0                              , 0                                , 1     , 0                  , 0, 0, 0),
                    ('fee-adjustment-test-company-2', 1112, 1      , 'Test Seller 1', 'SELLER', 'www.test-seller-1.com', 'a test seller', 0                  , 0         , 0               , 0                         , 0                , 1                        , 0                              , 0                                , 1     , 0                  , 0, 0, 0),
                    ('fee-adjustment-test-company-3', 1113, 1      , 'Test Seller 2', 'SELLER', 'www.test-seller-2.com', 'a test seller', 0                  , 0         , 0               , 0                         , 0                , 1                        , 0                              , 0                                , 1     , 0                  , 0, 0, 0);



INSERT INTO fee_adjustment (pid, name                         , inclusive, demand_fee_adjustment, version, enabled, description             , last_update          , creation_date        )
VALUES                     (1  , 'fee-adjustment-repository-1', true     , 0.1                  , 0      , true   , 'A test fee adjustment.', '2020-08-31 12:00:00', '2020-07-01 12:00:00'),
                           (2  , 'fee-adjustment-repository-2', true     , 0.2                  , 0      , false  , 'A test fee adjustment.', '2020-08-31 12:00:00', '2020-07-01 12:00:00'),
                           (3  , 'fee-adjustment-repository-3', false    , 0.3                  , 0      , true   , 'A test fee adjustment.', '2020-08-31 12:00:00', '2020-07-01 12:00:00');



INSERT INTO fee_adjustment_seller (pid, fee_adjustment_pid, seller_pid)
VALUES                            (1  , 1                 , 1112      ),
                                  (2  , 1                 , 1113      ),
                                  (3  , 2                 , 1113      );



INSERT INTO fee_adjustment_buyer (pid, fee_adjustment_pid, buyer_pid)
VALUES                           (1  , 1                 , 1111     ),
                                 (2  , 2                 , 1111     );
