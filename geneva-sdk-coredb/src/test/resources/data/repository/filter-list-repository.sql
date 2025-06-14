INSERT INTO deny_allow_filter_list (pid, company_pid, name, upload_status, type, invalid, duplicate, error, total, status, version)
VALUES (1, 2, 'Filter List 1', 'READY', 'DOMAIN', 0, 0, 0, 0, 1, 0),
(2, 2, 'Filter List 2', 'PENDING', 'DOMAIN', 0, 0, 0, 0, 1, 0),
(3, 2, 'Filter List 3', 'READY', 'APP', 0, 0, 0, 0, 1, 0),
(4, 3, 'Filter List 4', 'ERROR', 'DOMAIN', 0, 0, 0, 0, 1, 0);

INSERT INTO app_bundle_data (pid, app_bundle_id)
VALUES (1, 'abc.xyz.com'),
(2, 'xyz.com'),
(3, 'abc.com');

INSERT INTO deny_allow_filter_list_app_bundle_data(pid, deny_allow_filter_list_id, app_bundle_data_id, status, version)
VALUES (1, 1, 1, 1, 1),
       (2, 2, 2, 0, 0),
       (3, 3, 3, 1, 1);
