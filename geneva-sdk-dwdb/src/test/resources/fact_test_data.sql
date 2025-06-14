INSERT INTO test_dw.dim_tag
    (id, site_id, adnet_id, tag_owner, name, status)
VALUES (789, 123, 456, 1, "tag789", 8);

INSERT INTO test_dw.dim_tag
    (id, site_id, adnet_id, tag_owner, name, status)
VALUES (1415, 1011, 1213, 1, "tag1415", 8);

INSERT INTO test_dw.dim_tag
    (id, site_id, adnet_id, tag_owner, name, status)
VALUES (2021, 1617, 1819, 1, "tag1819", 8);

INSERT INTO test_dw.fact_screened_ad (start, stop, site_id, adnet_id, ad_id, seen_count)
VALUES (STR_TO_DATE('Feb 26, 2012', '%M %d, %Y'), STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 1, 1, 1,
        100);
INSERT INTO test_dw.fact_screened_ad (start, stop, site_id, adnet_id, ad_id, seen_count)
VALUES (STR_TO_DATE('Feb 26, 2012', '%M %d, %Y'), STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 1, 2, 2,
        300);
