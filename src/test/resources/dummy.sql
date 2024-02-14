-- 7명의 멤버가 존재합니다.
-- 1번, 2번, 3번, 4번 멤버는 활성화 상태입니다.
-- 5번, 6번, 7번 멤버는 비활성화 상태입니다.
-- 1번, 3번, 4번, 5번, 6번 멤버는 방금 생성되었습니다.
-- 2번, 7번 멤버는 1일 전에 생성되었습니다.
-- 3번, 5번 멤버는 2일 전에 생성되었습니다.

insert into member (member_id, email, name, nickname, role, contributes, image_url, refresh_token, social_type,
                    social_id, created_at, updated_at, active)
values (1, 'email1', 'name1', 'nickname1', 'USER', 0, 'image_url1', 'refresh_token1', 'KAKAO', 'social_id1', NOW(),
        NOW(), true),
       (2, 'email2', 'name2', 'nickname2', 'USER', 0, 'image_url2', 'refresh_token2', 'GOOGLE', 'social_id2',
        TIMESTAMPADD(DAY, -1, NOW()),
        NOW(), true),
       (3, 'email3', 'name3', 'nickname3', 'USER', 0, 'image_url3', 'refresh_token3', 'NAVER', 'social_id3',
        TIMESTAMPADD(DAY, -2, NOW()),
        NOW(), true),
       (4, 'email4', 'name4', 'nickname4', 'USER', 0, 'image_url4', 'refresh_token4', 'GOOGLE', 'social_id4', NOW(),
        NOW(), true),
       (5, 'email5', 'name5', 'nickname5', 'USER', 0, 'image_url5', 'refresh_token5', 'KAKAO', 'social_id5',
        TIMESTAMPADD(DAY, -2, NOW()),
        NOW(), false),
       (6, 'email6', 'name6', 'nickname6', 'USER', 0, 'image_url6', 'refresh_token6', 'KAKAO', 'social_id6', NOW(),
        NOW(), false),
       (7, 'email7', 'name7', 'nickname7', 'USER', 0, 'image_url7', 'refresh_token7', 'KAKAO', 'social_id7',
        TIMESTAMPADD(DAY, -1, NOW()),
        NOW(), false);

-- 4개의 문서가 존재합니다.
insert into document (document_id, title, latest_revision, parent_document_id, created_at, updated_at)
values (1, 'title1', 3, null, NOW(), NOW()),
       (2, 'title2', 2, null, NOW(), NOW()),
       (3, 'title3', 1, null, NOW(), NOW()),
       (4, 'title4', 1, null, NOW(), NOW());

-- member가 작성한 문서가 존재합니다.
-- 1번 멤버는 11, 12, 13, 14번 문서를 작성하였습니다.
-- 2번 멤버는 15, 16, 18번 문서를 작성하였습니다.
-- 3번 멤버는 17번 문서를 작성하였습니다.
-- 4번 멤버는 19번 문서를 작성하였습니다.
-- 5번 멤버는 20, 23, 24번 문서를 작성하였습니다.
-- 6번 멤버는 21번 문서를 작성하였습니다.
-- 7번 멤버는 22번 문서를 작성하였습니다.
insert into document (document_id, title, latest_revision, author_id, parent_document_id, created_at, updated_at)
values (11, 'title1', 3, 1, null, NOW(), NOW()),
       (12, 'title2', 2, 1, null, NOW(), NOW()),
       (13, 'title3', 1, 1, null, NOW(), NOW()),
       (14, 'title4', 1, 1, null, NOW(), NOW()),
       (15, 'title5', 1, 2, null, NOW(), NOW()),
       (16, 'title6', 1, 2, null, NOW(), NOW()),
       (17, 'title7', 1, 3, null, NOW(), NOW()),
       (18, 'title8', 1, 2, null, NOW(), NOW()),
       (19, 'title9', 1, 4, null, NOW(), NOW()),
       (20, 'title10', 1, 5, null, NOW(), NOW()),
       (21, 'title11', 1, 6, null, NOW(), NOW()),
       (22, 'title12', 1, 7, null, NOW(), NOW()),
       (23, 'title13', 1, 5, null, NOW(), NOW()),
       (24, 'title14', 1, 5, null, NOW(), NOW());

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
insert
into section (section_id, revision, document_id, heading, title, content, orders, created_at, updated_at)
values (1, 1, 1, 'H1', 'document1_title1', 'document1_content1\n', 1, NOW(), NOW()),
       (1, 3, 1, null, null, null, 1, NOW(), NOW()),
       (2, 1, 1, 'H2', 'document1_title2', 'document1_content2\n', 2, NOW(), NOW()),
       (2, 2, 1, 'H2', 'document1_title2_update', 'document1_content2_update\n', 2, NOW(), NOW()),
       (3, 1, 1, 'H3', 'document1_title3', 'document1_content3\n', 3, NOW(), NOW()),
       (13, 2, 1, 'H1', 'document1_title4_insert', 'document1_content4_insert\n', 4, NOW(), NOW()),
       (4, 1, 2, 'H1', 'document2_title1', 'document2_content1\n', 1, NOW(), NOW()),
       (5, 1, 2, 'H2', 'document2_title2', 'document2_content2\n', 3, NOW(), NOW()),
       (6, 1, 2, 'H3', 'document2_title3', 'document2_content3\n', 4, NOW(), NOW()),
       (14, 2, 2, 'H1', 'document2_title4_insert', 'document2_content4_insert\n', 2, NOW(), NOW()),
       (7, 1, 3, 'H1', 'document3_title1', 'document3_content1\n', 1, NOW(), NOW()),
       (8, 1, 3, 'H2', 'document3_title2', 'document3_content2\n', 2, NOW(), NOW()),
       (9, 1, 3, 'H3', 'document3_title3', 'document3_content3\n', 3, NOW(), NOW()),
       (10, 1, 4, 'H1', 'document4_title1', 'document4_content1\n', 1, NOW(), NOW()),
       (11, 1, 4, 'H2', 'document4_title2', 'document4_content2\n', 2, NOW(), NOW()),
       (12, 1, 4, 'H3', 'document4_title3', 'document4_content3\n', 3, NOW(), NOW());

-- SectionId의 sequence_Id는 15부터
INSERT into sequence_table (sequence_name, sequence_value)
values ('section', 15);


-- 5개의 contribute가 존재합니다.
------ 4번 Contribute는 5번 admentment를 갖고있는 수정요청으로, 거절되었습니다.
------ 5번은 6, 7번 admentment를 갖고 있는 수정요청으로, 투표중입니다.
insert into contribute (contribute_id, member_id, document_id, title, description, status,
                        before_document_title, after_document_title,
                        before_parent_document_id, after_parent_document_id,
                        created_at, updated_at)
values (1, 1, 1, 'contribute_title1', 'contribute_description1', 'MERGED', 'title1', 'title1', null, null,
        '2024-03-21 00:00:00', NOW()),
       (2, 2, 1, 'contribute_title2', 'contribute_description2', 'MERGED', 'title1', 'title1', null, null,
        '2024-03-21 00:01:00', NOW()),
       (3, 3, 2, 'contribute_title3', 'contribute_description3', 'MERGED', 'title2', 'title2', null, null,
        '2024-03-21 00:02:00', NOW()),
       (4, 1, 2, 'contribute_title4', 'contribute_description4', 'REJECTED', 'title2', 'title2', null, null,
        '2024-03-21 00:03:00', NOW()),
       (5, 2, 2, 'contribute_title5', 'contribute_description5', 'VOTING', 'title2', 'new_title_2', null, null,
        '2024-03-21 00:04:00', NOW()),
       (6, 1, 3, 'contribute_title6', 'contribute_description6', 'DEBATING', 'title3', 'new_title_3', null, null,
        '2024-03-21 00:05:00', NOW()),
       (7, 5, 4, 'contribute_title7', 'contribute_description7', 'MERGED', 'title4', 'new_title_4', null, null,
        '2024-03-21 00:06:00', NOW()),
       (8, 5, 4, 'contribute_title8', 'contribute_description8', 'REJECTED', 'title4', 'new_title_4', null, null,
        '2024-03-21 00:07:00', NOW()),
       (9, 5, 4, 'contribute_title9', 'contribute_description9', 'VOTING', 'title4', 'new_title_4', null, null,
        '2024-03-21 00:06:00', NOW());


-- 7개의 admentment가 존재합니다.
------ 1번 amendment는 1번 contribute에 포함되어있으며, 섹션 2번을 업데이트하고자하는 수정안입니다.
------ 2번 amendment는 1번 contribute에 포함되어있으며, 섹션 3번 뒤에 새로운 섹션을 생성하고자하는 수정안입니다.
------ 3번 amendment는 2번 contribute에 포함되어있으며, 섹션 1번을 삭제하고자하는 수정안입니다.
------ 4번 amendment는 수정요청에 등록되지 않았으며,, 섹션 13번을 업데이트하고자하는 수정안입니다.
------ 5번 amendment는 4번 contribute에 포함되어있으며, 섹션 3번을 업데이트하고자하는 수정안입니다.
------ 6번 amendment는 5번 contribute에 포함되어있으며, 섹션 4번 뒤에 새로운 섹션을 생성하고자하는 수정안입니다.
------ 7번 amendment는 5번 contribute에 포함되어있으며, 섹션 6번을 업데이트하고자하는 수정안입니다.
insert into amendment (amendment_id, contribute_id, target_section_id, target_revision, creating_order,
                       new_section_heading, new_section_title,
                       new_section_content, type, created_at, updated_at)
values (1, 1, 2, 1, null, 'H2', 'document1_title2_update', 'document1_content2_update', 'UPDATE', NOW(), NOW()),
       (2, 1, 3, 1, 1, 'H1', 'document1_title4_insert', 'document1_content4_insert', 'CREATE', NOW(), NOW()),
       (3, 2, 1, 1, null, NULL, NULL, NULL, 'DELETE', NOW(), NOW()),
       (4, null, 13, 2, null, 'H1', 'document1_title4_insert_update',
        'document1_content4_insert_update', 'UPDATE', NOW(), NOW()),
       (5, 4, 3, 1, null, 'H2', 'document1_title3', 'document1_content3', 'UPDATE', NOW(), NOW()),
       (6, 5, 4, 1, 2, 'H1', 'document2_title4_insert', 'document2_content4_insert', 'CREATE', NOW(), NOW()),
       (7, 5, 6, 1, null, 'H1', 'document2_title3_update', 'document2_content3_update', 'UPDATE', NOW(), NOW());

insert into debate (debate_id, contribute_id, status, end_at, comment_sequence, created_at)
values (1, 1, 'OPEN', TIMESTAMPADD(HOUR, 1, NOW()), 1, TIMESTAMPADD(HOUR, -3, NOW())),
       (2, 2, 'OPEN', TIMESTAMPADD(MINUTE, -1, NOW()), 1, TIMESTAMPADD(HOUR, -1, NOW())),
       (3, 3, 'OPEN', TIMESTAMPADD(MINUTE, -1, NOW()), 1, NOW()),
       (4, 4, 'CLOSED', NOW(), 1, NOW()),
       (5, 5, 'CLOSED', NOW(), 1, NOW()),
       (6, 5, 'OPEN', NOW(), 1, NOW()),
       (7, 6, 'OPEN', NOW(), 1, NOW());

insert into comment (comment_id, debate_id, commenter_id, content, sequences, created_at)
values (1, 1, 1, '댓글1', 1, TIMESTAMPADD(HOUR, -3, NOW())),
       (2, 1, 2, '댓글2', 2, TIMESTAMPADD(HOUR, -2, NOW())),
       (3, 1, 3, '댓글3', 3, TIMESTAMPADD(HOUR, -1, NOW())),
       (4, 2, 4, '댓글4', 1, TIMESTAMPADD(HOUR, -4, NOW())),
       (5, 2, 1, '댓글5', 2, TIMESTAMPADD(HOUR, -3, NOW())),
       (6, 2, 2, '댓글6', 3, TIMESTAMPADD(HOUR, -3, NOW())),
       (7, 3, 3, '댓글7', 1, TIMESTAMPADD(HOUR, -5, NOW())),
       (8, 3, 4, '댓글8', 2, TIMESTAMPADD(HOUR, -10, NOW())),
       (9, 3, 4, '댓글9', 3, TIMESTAMPADD(HOUR, -2, NOW()));

-- 5개의 Bookmark가 존재합니다.
-- 1번 멤버는 1, 2, 3, 4번 문서를 북마크하였습니다.
-- 2번 멤버는 1, 4번 문서를 북마크하였습니다.
-- 3번 멤버는 2, 3, 4번 문서를 북마크하였습니다.
-- 4번 멤버는 3, 4번 문서를 북마크하였습니다.
insert into bookmark (bookmark_id, member_id, document_id)
values (1, 1, 1),
       (2, 2, 1),
       (3, 3, 2),
       (4, 4, 3),
       (5, 1, 4),
       (6, 2, 4),
       (7, 3, 3),
       (8, 1, 2),
       (10, 1, 3),
       (11, 3, 4),
       (12, 4, 4);

insert into vote (vote_id, contribute_id, member_id, agree, created_at)
values (1, 5, 1, true, TIMESTAMPADD(HOUR, -3, NOW())),
       (2, 5, 2, true, TIMESTAMPADD(HOUR, -2, NOW())),
       (3, 5, 3, true, TIMESTAMPADD(HOUR, -1, NOW())),
       (4, 5, 4, false, TIMESTAMPADD(HOUR, -4, NOW())),
       (5, 9, 1, true, TIMESTAMPADD(HOUR, -3, NOW())),
       (6, 9, 2, true, TIMESTAMPADD(HOUR, -3, NOW())),
       (7, 9, 3, false, TIMESTAMPADD(HOUR, -5, NOW())),
       (8, 9, 4, false, TIMESTAMPADD(HOUR, -10, NOW()))
