INSERT INTO brand_protection_category (pid, name, archive_date, update_date)
VALUES (1, 'Creative Attribute', null, null),
       (2, 'Creative Spec', null, null);

INSERT INTO brand_protection_tag (pid, category_pid, name, rtb_id, free_text_tag, update_date,
                                  parent_tag_pid)
VALUES (18, 2, 'ActiveX', null, 0, null, null),
       (19, 2, 'Oath Banned Tracker', null, 0, null, null),
       (20, 2, 'Oath Unapproved Beacon', null, 0, null, null);

INSERT INTO crs_tag_mapping (pid, bprot_tag_pid, crs_tag_id, crs_tag_attribute_id, update_date)
VALUES (1, 18, 2, 3, '2021-07-07 04:26:14');

INSERT INTO crs_tag_mapping (pid, bprot_tag_pid, crs_tag_id, crs_tag_attribute_id, update_date)
VALUES (2, 19, 2, 3, '2021-07-07 04:26:14');
