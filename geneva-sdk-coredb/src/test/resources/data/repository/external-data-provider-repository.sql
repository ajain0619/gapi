INSERT INTO external_data_provider (pid, name, base_url, enablement_status, last_update, description, data_provider_impl_class, filter_request_rate, VERSION, creation_date, bid_request_attr_name, bid_request_location, configuration, bidder_alias_required)
VALUES (1, 'ConnectPartner_1', 'https://gggoooo.com', 1, '2015-09-26 14:30:16', 'desc1', 'ic1', 0, 2, '2015-09-10 14:43:35', 'bra1', 1, 'config1', true);

INSERT INTO external_data_provider (pid, name, base_url, enablement_status, last_update, description, data_provider_impl_class, filter_request_rate, VERSION, creation_date, bid_request_attr_name, bid_request_location, configuration, bidder_alias_required)
VALUES (2, 'ConnectPartner_2', 'https://nexage.com', 0, '2015-09-25 14:31:03', 'desc2', 'ic2', 0, 0, '2015-09-23 14:31:03', 'bra2', 0, 'config2', false);

INSERT INTO external_data_provider (pid, name, base_url, enablement_status, last_update, description, data_provider_impl_class, filter_request_rate, VERSION, creation_date, bid_request_attr_name, bid_request_location, configuration, bidder_alias_required)
VALUES (3, 'ConnectPartner_3', 'https://nexage.com', 0, '2015-09-24 14:31:03', 'desc3', 'ic3', 0, 0, '2015-09-24 14:31:03', 'bra3', 0, 'config3', false);

INSERT INTO exchange_prod (pid, regional_id, name)
VALUES (1, 2, 'exchange-1');

INSERT INTO exchange_prod (pid, regional_id, name)
VALUES (2, 1, 'exchange-2');
