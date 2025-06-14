INSERT INTO ad_source(pid, ad_type, class_name, creation_date, description, last_update, name, url_template, version, company_pid,
                      ad_screening, bid_enabled, decision_maker_enabled, disable_click_wrap, header_pass_through,
                      id, self_serve_enablement, status, use_wrapped_sdk)
  VALUES (1, 1, 'classname', '2019-01-01 10:11:12', 'description', '2019-02-01 10:00:00', 'name', 'url template', 1, 1,
          0, 0, 1, 0, 0, '123455', 1, 1, 1);

INSERT INTO tag(pid,name, site_pid, status, primary_id, ecpm_provision, owner, version, position_pid)
  VALUES(2, 'sometag', 1, 1, 'primaryid', 'ecm', 1, 1, 2);

INSERT INTO exchange_site_tag(pid, creation_date, tag_id, alter_reserve, version)
  VALUES(1, '2019-01-02 10:00:00', 'primaryid', 1, 1);

INSERT INTO deal_term(pid, site_pid, tag_pid, effective_date, revenue_mode, nexage_rev_share, rtb_fee, version)
  VALUES(1, 1, 2, '2020-01-10 08:00:00', 'REV_SHARE', 0.005, 0.002, 1);
INSERT INTO deal_term(pid, site_pid, tag_pid, effective_date, revenue_mode, nexage_rev_share, rtb_fee, version)
  VALUES(2, 1, 2, '2020-01-01 08:00:00', 'REV_SHARE', 0.003, 0.002, 1);
