INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (1, 0, 5, 1, 'deal', 2, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, NULL, 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (2, 0, 6, 1, 'Test-deal', 2, '2020-06-10 13:10:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', false, 3, 'formula 1', 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (111, 0, '5550', 1, 'deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, 'formula 2', 0);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (110, 0, '5551', 1, 'deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 100,
        '2020-06-20 10:00:00', true, 1, 'formula 3', 1);

INSERT INTO deal(pid, version, id, status, description, created_by, creation_date, currency,
                 all_sellers, all_bidders,
                 priority_type, updated_on, visibility, deal_category, placement_formula,
                 auto_update)
VALUES (112, 0, 'deal-id', 0, 'x-deal', 21, '2020-06-10 13:00:00', 'USD', false, false, 200,
        '2020-06-20 10:00:00', true, 2, 'formula 3', 1);

INSERT INTO deal_rule(pid, deal_pid, rule_group_pid, version)
values (1, 1, 5, 0);

INSERT INTO deal_rule(pid, deal_pid, rule_group_pid, version)
values (2, 2, 1, 0);

INSERT INTO deal_rule(pid, deal_pid, rule_group_pid, version)
values (3, 1, 2, 0);
