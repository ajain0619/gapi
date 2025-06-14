
insert into bdr_publisher_info (pid)
VALUES (1), (2), (3), (4), (5);


insert into bdr_site_info (pid, bdr_pub_info_pid, type, iab_categories, exchange_ext_id)
VALUES (1,1,'type1','category1',0), (2,1,'type1','category1,category2,category3',0),
    (3,2,'type2','category1,category2',0), (4,2,'type2','category2',0), (5,1,'type3','category3',0);
