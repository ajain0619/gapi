INSERT INTO brand_protection_category (pid, name, archive_date, update_date)
VALUES (1, 'Creative Attribute', null, null),
       (2, 'Creative Spec', null, null),
       (6, 'IAB Category', null, null);

INSERT INTO brand_protection_tag (pid, category_pid, name, rtb_id, free_text_tag, update_date,
                                  parent_tag_pid)
VALUES (18, 2, 'ActiveX', null, 0, null, null),
       (19, 2, 'Oath Banned Tracker', null, 0, null, null),
       (20, 2, 'Oath Unapproved Beacon', null, 0, null, null),
       (670, 6, 'Business', 'IAB3', 0, null, null),
       (283, 6, 'Advertising', 'IAB3-1', 0, null, 670),
       (284, 6, 'Agriculture', 'IAB3-2', 0, null, 670),
       (285, 6, 'Biotech/Biomedical', 'IAB3-3', 0, null, 670),
       (682, 6, 'Science', 'IAB15', 0, null, null),
       (491, 6, 'Astrology', 'IAB15-1', 0, null, 682),
       (492, 6, 'Biology', 'IAB15-2', 0, null, 682),
       (493, 6, 'Chemistry', 'IAB15-3', 0, null, 682);
