INSERT INTO rule (pid, company_pid, version, status, name, description, last_update)
VALUES (1, 1, 1, 1, 'Test Rule One', 'Test Rule', now()),
       (2, 1, 1, -1, 'Test Rule Two', 'Test Rule', now()),
       (3, 1, 1, 1, 'Test Rule Three', 'Test Rule', now());

INSERT INTO rule_target(pid, version, status, match_type, target_type, data, rule_pid)
VALUES (1, 0, 1, 1, 22, '[{"buyerCompany":10205}]', 1),
       (2, 0, 1, 1, 22, '[{"buyerCompany":10205"},{"buyerCompany":300}]', 2),
       (3, 0, 1, 1, 22, '[{"buyerCompany":10205"}', 3),
       (4, 0, 1, 1, 33, 'Connected Device,Set Top Box,Connected TV', 3);
