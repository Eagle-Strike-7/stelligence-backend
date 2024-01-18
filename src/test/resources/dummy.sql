-- 4명의 멤버가 존재합니다.
insert into member (member_id, email, name, nickname, role, contributes, image_url, refresh_token, social_type,
                    social_id, created_at, updated_at)
values (1, 'email1', 'name1', 'nickname1', 'USER', 0, 'image_url1', 'refresh_token1', 'KAKAO', 'social_id1', NOW(), NOW()),
       (2, 'email2', 'name2', 'nickname2', 'USER', 0, 'image_url2', 'refresh_token2', 'GOOGLE', 'social_id2', NOW(), NOW()),
       (3, 'email3', 'name3', 'nickname3', 'USER', 0, 'image_url3', 'refresh_token3', 'NAVER', 'social_id3', NOW(), NOW()),
       (4, 'email4', 'name4', 'nickname4', 'USER', 0, 'image_url4', 'refresh_token4', 'GOOGLE', 'social_id4', NOW(), NOW());

-- 4개의 문서가 존재합니다.
insert into document (document_id, title, current_revision, created_at, updated_at)
values (1, 'title1', 3, NOW(), NOW()),
       (2, 'title2', 2, NOW(), NOW()),
       (3, 'title3', 1, NOW(), NOW()),
       (4, 'title4', 1, NOW(), NOW());

-- 14개의 섹션이 존재합니다.
------ 1번 문서에는 6개의 섹션이 존재합니다.
---------- (1,1) (2,1) (3,1) 은 최초의 1번 문서로 생성된 섹션입니다.
---------- (2,2) (13,2) 는 Contribute 1번에 의하여 반영된 섹션입니다. (업데이트와 생성)
------ (1,3) 은 Contribute 2번에 의하여 반영된 섹션입니다. (삭제)
---------- 2번 문서에는 4개의 섹션이 존재합니다.
------ (4,1) (5,1) (6,1) 은 최초의 2번 문서로 생성된 섹션입니다.
---------- (14,2) 는 Contribute 3번에 의하여 반영된 섹션입니다. (생성)
------ 3번 문서에는 3개의 섹션이 존재합니다.
---------- (7,1) (8,1) (9,1) 은 최초의 3번 문서로 생성된 섹션입니다.
------ 4번 문서에는 3개의 섹션이 존재합니다.
----------- (10,1) (11,1) (12,1) 은 최초의 4번 문서로 생성된 섹션입니다.
-- 관련 링크 : https://excited-cycle-902.notion.site/762ff798869f4320aec03bb4fbff27c9?pvs=4
insert into section (section_id, revision, document_id, heading, title, content, orders, created_at, updated_at)
values (1, 1, 1, 'H1', 'document1_title1', 'document1_content1', 1, NOW(), NOW()),
       (1, 3, 1, null, null, null, 1, NOW(), NOW()),
       (2, 1, 1, 'H2', 'document1_title2', 'document1_content2', 2, NOW(), NOW()),
       (2, 2, 1, 'H2', 'document1_title2_update', 'document1_content2_update', 2, NOW(), NOW()),
       (3, 1, 1, 'H3', 'document1_title3', 'document1_content3', 3, NOW(), NOW()),
       (13, 2, 1, 'H1', 'document1_title4_insert', 'document1_content4_insert', 4, NOW(), NOW()),
       (4, 1, 2, 'H1', 'document2_title1', 'document2_content1', 1, NOW(), NOW()),
       (5, 1, 2, 'H2', 'document2_title2', 'document2_content2', 3, NOW(), NOW()),
       (6, 1, 2, 'H3', 'document2_title3', 'document2_content3', 4, NOW(), NOW()),
       (14, 2, 2, 'H1', 'document2_title4_insert', 'document2_content4_insert', 2, NOW(), NOW()),
       (7, 1, 3, 'H1', 'document3_title1', 'document3_content1', 1, NOW(), NOW()),
       (8, 1, 3, 'H2', 'document3_title2', 'document3_content2', 2, NOW(), NOW()),
       (9, 1, 3, 'H3', 'document3_title3', 'document3_content3', 3, NOW(), NOW()),
       (10, 1, 4, 'H1', 'document4_title1', 'document4_content1', 1, NOW(), NOW()),
       (11, 1, 4, 'H2', 'document4_title2', 'document4_content2', 2, NOW(), NOW()),
       (12, 1, 4, 'H3', 'document4_title3', 'document4_content3', 3, NOW(), NOW());

-- SectionId의 sequence_Id는 15부터
INSERT into sequence_table (sequence_name, sequence_value)
values ('section', 15);


-- 5개의 contribute가 존재합니다.
------ 4번 Contribute는 5번 admentment를 갖고있는 수정요청으로, 거절되었습니다.
------ 5번은 6, 7번 admentment를 갖고 있는 수정요청으로, 투표중입니다.
insert into contribute (id, status, created_at, updated_at)
values (1, 'MERGED', NOW(), NOW()),
       (2, 'MERGED', NOW(), NOW()),
       (3, 'MERGED', NOW(), NOW()),
       (4, 'REJECTED', NOW(), NOW()),
       (5, 'VOTING', NOW(), NOW());


-- 7개의 admentment가 존재합니다.
------ 1번 amendment는 1번 contribute에 포함되어있으며, 섹션 2번을 업데이트하고자하는 수정안입니다.
------ 2번 amendment는 1번 contribute에 포함되어있으며, 섹션 3번 뒤에 새로운 섹션을 생성하고자하는 수정안입니다.
------ 3번 amendment는 2번 contribute에 포함되어있으며, 섹션 1번을 삭제하고자하는 수정안입니다.
------ 4번 amendment는 수정요청에 등록되지 않았으며,, 섹션 13번을 업데이트하고자하는 수정안입니다.
------ 5번 amendment는 4번 contribute에 포함되어있으며, 섹션 3번을 업데이트하고자하는 수정안입니다.
------ 6번 amendment는 5번 contribute에 포함되어있으며, 섹션 4번 뒤에 새로운 섹션을 생성하고자하는 수정안입니다.
------ 7번 amendment는 5번 contribute에 포함되어있으며, 섹션 6번을 업데이트하고자하는 수정안입니다.
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



