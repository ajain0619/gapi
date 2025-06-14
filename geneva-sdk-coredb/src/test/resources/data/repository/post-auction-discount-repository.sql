INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking, enable_rtb, enable_mediation, rtb_revenue_report_enabled, selfserve_allowed, default_rtb_profiles_enabled, dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback, external_ad_verification_enabled, third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES              ('company-1', 1114, 1, 'Test Seller 1', 'SELLER', 'www.test-seller-1.com', 'a test seller', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0),
                    ('company-2', 1115, 1, 'Test Seller 2', 'SELLER', 'www.test-seller-2.com', 'a test seller', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0),
                    ('company-3', 1116, 1, 'Test Seller 3', 'SELLER', 'www.test-seller-3.com', 'a test seller', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0),
                    ('company-4', 1117, 1, 'Test Seller 4', 'SELLER', 'www.test-seller-4.com', 'a test seller', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES              (1, 'buyer_group_1', 1114, 'sfdc_line_id_1', 'sfdc_io_id_1', 'USD', 'USA', true, 0),
                    (2, 'buyer_group_2', 1115, 'sfdc_line_id_2', 'sfdc_io_id_2', 'USD', 'USA', true, 0),
                    (3, 'buyer_group_3', 1116, 'sfdc_line_id_3', 'sfdc_io_id_3', 'USD', 'USA', true, 0),
                    (4, 'buyer_group_4', 1117, 'sfdc_line_id_4', 'sfdc_io_id_4', 'USD', 'USA', true, 0);

INSERT INTO buyer_seat(pid, company_pid, name, seat, buyer_group_pid, enabled, version,
                       creation_date, last_updated_date)
VALUES              (1111, 1114, 'Test Buyer Seat 1', 'Seat 1', 1, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (1112, 1115, 'Test Buyer Seat 2', 'Seat 2', 2, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (1113, 1116, 'Test Buyer Seat 3', 'Seat 3', 3, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (1114, 1117, 'Test Buyer Seat 4', 'Seat 4', 4, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00');

INSERT INTO post_auction_discount(pid, discount_name, discount_description, discount_percent, discount_status,
                                  open_auction_enabled, version, last_update, creation_date)
VALUES              (1, 'post-auction-discount-1', 'a post auction discount', 10.2, true, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (2, 'post-auction-discount-2', 'a post auction discount', 25.0, false, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (3, 'post-auction-discount-3', 'a post auction discount', 10.2, true, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (4, 'post-auction-discount-4', 'a post auction discount', 25.0, false, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (5, 'post-auction-discount-5', 'a post auction discount', 10.2, true, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (6, 'post-auction-discount-6', 'a post auction discount', 25.0, false, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (7, 'post-auction-discount-7', 'a post auction discount', 10.2, true, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (8, 'post-auction-discount-8', 'a post auction discount', 25.0, false, true, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00');

INSERT INTO post_auction_discount_dsp_seat(pid, post_auction_discount_pid, dsp_seat_pid, version, last_update, creation_date)
VALUES              (1, 1, 1111, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (2, 2, 1111, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (3, 3, 1112, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (4, 4, 1112, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (5, 5, 1113, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (6, 6, 1113, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (7, 7, 1114, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00'),
                    (8, 8, 1114, 2, '2021-04-01 12:00:00', '2021-03-31 12:00:00');

INSERT INTO post_auction_discount_type(pid, name, updated_on, created_on)
VALUES              (1, 'pad v1', NOW(), NOW()),
                    (2, 'pad v2', NOW(), NOW());

INSERT INTO post_auction_discount_seller(pid, post_auction_discount_pid, company_pid, type_pid, last_update, creation_date, version)
VALUES              (1, 1, 1114, 1, '2021-04-01 12:00:00', '2021-03-31 12:00:00', 2),
                    (2, 2, 1114, 1, '2021-04-01 12:00:00', '2021-03-31 12:00:00', 2),
                    (3, 5, 1115, 1, '2021-04-01 12:00:00', '2021-03-31 12:00:00', 2),
                    (4, 8, 1116, 1, '2021-04-01 12:00:00', '2021-03-31 12:00:00', 2);

INSERT INTO revenue_group(pid, id, revenue_group_name)
VALUES              (1, 'revenue-group-1', 'Revenue Group 1'),
                    (2, 'revenue-group-2', 'Revenue Group 2'),
                    (3, 'revenue-group-3', 'Revenue Group 3');

INSERT INTO post_auction_discount_revenue_group(pid, post_auction_discount_pid, revenue_group_pid, type_pid)
VALUES              (1, 3, 1, 1),
                    (2, 4, 1, 1),
                    (3, 6, 2, 1),
                    (4, 7, 3, 1);

INSERT INTO seller_attributes(seller_pid, revenue_group_pid, version)
VALUES              (1115, 2, 2),
                    (1116, 3, 2);
