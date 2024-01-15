insert into member (member_id, email, name, nickname, role, contributes, image_url, refresh_token, social_type,
                    social_id, created_at, updated_at)
values (1, 'email1', 'name1', 'nickname1', 'USER', 0, 'image_url1', 'refresh_token1', 0, 'social_id1', NOW(), NOW()),
       (2, 'email2', 'name2', 'nickname2', 'USER', 0, 'image_url2', 'refresh_token2', 0, 'social_id2', NOW(), NOW()),
       (3, 'email3', 'name3', 'nickname3', 'USER', 0, 'image_url3', 'refresh_token3', 0, 'social_id3', NOW(), NOW()),
       (4, 'email4', 'name4', 'nickname4', 'USER', 0, 'image_url4', 'refresh_token4', 0, 'social_id4', NOW(), NOW());

insert into document (document_id, title, current_revision, created_at, updated_at)
values (1, 'title1', 3, NOW(), NOW()),
       (2, 'title2', 2, NOW(), NOW()),
       (3, 'title3', 1, NOW(), NOW()),
       (4, 'title4', 1, NOW(), NOW());

insert into section (section_id, revision, document_id, heading, title, content, orders, created_at, updated_at)
values (1, 1, 1, 'H1', 'document1_title1', 'document1_content1', 1, NOW(), NOW()),
       (1, 3, 1, null, null, null, 1, NOW(), NOW()),
       (2, 1, 1, 'H2', 'document1_title2', 'document1_content2', 2, NOW(), NOW()),
       (2, 2, 1, 'H2', 'document1_title2_update', 'document1_content2_update', 2, NOW(), NOW()),
       (3, 1, 1, 'H3', 'document1_title3', 'document1_content3', 3, NOW(), NOW()),
       (13, 2, 1, 'H1', 'document1_title4_insert', 'document1_content4_insert', 4, NOW(), NOW()),
       (4, 1, 2, 'H1', 'document2_title1', 'document2_content1', 1, NOW(), NOW()),
       (5, 1, 2, 'H2', 'document2_title2', 'document2_content2', 3, NOW(), NOW()),
       (6, 1, 2, 'H3', 'document2_title3', 'document3_content3', 4, NOW(), NOW()),
       (14, 2, 2, 'H1', 'document2_title4_insert', 'document2_content4_insert', 2, NOW(), NOW()),
       (7, 1, 3, 'H1', 'document3_title1', 'document3_content1', 1, NOW(), NOW()),
       (8, 1, 3, 'H2', 'document3_title2', 'document3_content2', 2, NOW(), NOW()),
       (9, 1, 3, 'H3', 'document3_title3', 'document3_content3', 3, NOW(), NOW()),
       (10, 1, 4, 'H1', 'document4_title1', 'document4_content1', 1, NOW(), NOW()),
       (11, 1, 4, 'H2', 'document4_title2', 'document4_content2', 2, NOW(), NOW()),
       (12, 1, 4, 'H3', 'document4_title3', 'document4_content3', 3, NOW(), NOW());

INSERT sequence_table (sequence_name, sequence_value)
values ('section', 15);



insert into contribute (id, status, created_at, updated_at)
values (1, 'MERGED', NOW(), NOW()),
       (2, 'MERGED', NOW(), NOW()),
       (3, 'MERGED', NOW(), NOW()),
       (4, 'REJECTED', NOW(), NOW()),
       (5, 'VOTING', NOW(), NOW());



insert into amendment (commit_id, contribute_id, member_id, target_section_id, target_revision,
                       amendment_title, amendment_description, new_section_heading, new_section_title,
                       new_section_content, status, type, created_at, updated_at)
values (1, 1, 1, 2, 1, 'amendment_title1', 'amendment_description1', 'H2', 'document1_title2_update',
        'document1_content2_update', 'REQUESTED', 'UPDATE', NOW(), NOW()),
       (2, 1, 1, 3, 1, 'amendment_title2', 'amendment_description2', 'H1', 'document1_title4_insert',
        'document1_content4_insert', 'REQUESTED', 'CREATE', NOW(), NOW()),
       (3, 2, 1, 1, 1, 'amendment_title3', 'amendment_description3', NULL, NULL,
        NULL, 'REQUESTED', 'DELETE', NOW(), NOW()),
       (4, null, 1, 13, 2, 'amendment_title4', 'amendment_description4', 'H1', 'document1_title4_insert_update',
        'document1_content4_insert_update', 'PENDING', 'UPDATE', NOW(), NOW()),
       (5, 4, 1, 3, 1, 'amendment_title5', 'amendment_description5', 'H2', 'document1_title3', 'document1_content3',
        'REQUESTED', 'UPDATE', NOW(), NOW()),
       (6, 5, 2, 4, 1, 'amendment_title6', 'amendment_description6', 'H1', 'document2_title4_insert',
        'document2_content4_insert', 'REQUESTED', 'CREATE', NOW(), NOW()),
       (7, 5, 2, 6, 1, 'amendment_title6', 'amendment_description6', 'H1', 'document2_title3_update',
        'document2_content3_update',
        'REQUESTED', 'UPDATE', NOW(), NOW());



