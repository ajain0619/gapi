INSERT INTO company (id, pid, version, name, type, url, description,
                     dynamic_buyer_registration_enabled, brxd_buyer_id_enabled_on_bid_request,
                     disable_ad_feedback, external_ad_verification_enabled,
                     third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3016', 500, 1, 'Company 1', 'SELLER', 'www.company1.com',
        'New Company', 0, 0, 0, 0, 0, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (1, 'buyer_group_1', 500, 'sfdc_line_id_1', 'sfdc_io_id_1', 'USD', 'USA', true, 0);

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (3, null, 1, 'name_3', 'seat_3', false, 0, now(), now());



INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (2, 'buyer_group_2', 500, 'sfdc_line_id_2', 'sfdc_io_id_2', 'USD', 'USA', true, 0);

INSERT INTO buyer_group (pid, name, company_pid, sfdc_line_id, sfdc_io_id, currency,
                         billing_country, billable, version)
VALUES (3, 'buyer_group_3', 500, 'sfdc_line_id_3', 'sfdc_io_id_3', 'USD', 'USA', true, 0);

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (1, 500, 2, 'name_1', 'seat_1', true, 0, now(), now());

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (2, 500, 2, 'name_2', 'seat_2', false, 0, now(), now());



INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (4, 500, 2, 'name_4', 'seat_4', true, 0, now(), now());

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (5, 500, 1, 'name_5', 'seat_5', true, 0, now(), now());

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (6, 500, 1, 'name_6', 'seat_6', false, 0, now(), now());

INSERT INTO buyer_seat (pid, company_pid, buyer_group_pid, name, seat, enabled, version,
                        creation_date, last_updated_date)
VALUES (7, 500, 1, 'name_777', 'seat_777', false, 0, null, null);

create table buyer_transparency_data_feed
(
  pid             bigint auto_increment
    primary key,
  id              varchar(255)                       not null,
  buyer_feed_name varchar(32)                        not null,
  aws_bucket_name varchar(32)                        null,
  status          tinyint  default 1                 not null,
  version         int      default 1                 not null,
  created_on      datetime default CURRENT_TIMESTAMP not null,
  updated_on      datetime default CURRENT_TIMESTAMP not null,
  constraint buyer_feed_name
    unique (buyer_feed_name),
  constraint id
    unique (id)
);

INSERT INTO buyer_transparency_data_feed (pid, id, buyer_feed_name, aws_bucket_name, status,
                                          version, created_on, updated_on)
VALUES (1, '1', '1', 'aws-bucket', 1, 0, now(), now());
