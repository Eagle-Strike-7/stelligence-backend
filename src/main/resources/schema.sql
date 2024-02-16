set foreign_key_checks = 0;

DROP TABLE IF EXISTS debate;
DROP TABLE IF EXISTS contribute;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS document;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS amendment;
DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS bookmark;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS member_badge;
DROP TABLE IF EXISTS withdrawn_member;
DROP TABLE IF EXISTS sequence_table;

set foreign_key_checks = 1;


CREATE TABLE member
(
    member_id     BIGINT                                      NOT NULL auto_increment,
    role          ENUM ('ADMIN','USER')                       NOT NULL,
    contributes   BIGINT                                      NOT NULL,
    active        TINYINT                                     NOT NULL,
    nickname      VARCHAR(50)                                 NOT NULL,
    email         VARCHAR(255),
    image_url     VARCHAR(255),
    social_id     VARCHAR(255),
    social_type   ENUM ('GOOGLE','NAVER','KAKAO','WHITDRAWN') NOT NULL,
    refresh_token VARCHAR(255),
    created_at    DATETIME(6)                                 NOT NULL,
    updated_at    DATETIME(6)                                 NOT NULL,
    PRIMARY KEY (member_id)
);

CREATE TABLE document
(
    document_id        BIGINT      NOT NULL auto_increment,
    author_id          BIGINT      NOT NULL,
    title              VARCHAR(20) NOT NULL,
    latest_revision    BIGINT      NOT NULL,
    parent_document_id BIGINT,
    created_at         DATETIME(6) NOT NULL,
    updated_at         DATETIME(6) NOT NULL,
    PRIMARY KEY (document_id)
);

CREATE TABLE section
(
    section_id  BIGINT      NOT NULL,
    revision    BIGINT      NOT NULL,
    document_id BIGINT      NOT NULL,
    heading     ENUM ('H1','H2','H3'),
    title       VARCHAR(100),
    content     TEXT,
    orders      INTEGER     NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (revision, section_id)
);

CREATE TABLE contribute
(
    contribute_id             BIGINT                                         NOT NULL auto_increment,
    status                    ENUM ('VOTING','DEBATING','REJECTED','MERGED') NOT NULL,
    member_id                 BIGINT                                         NOT NULL,
    document_id               BIGINT                                         NOT NULL,
    title                     VARCHAR(100)                                   NOT NULL,
    description               VARCHAR(5000)                                  NOT NULL,
    before_document_title     VARCHAR(20)                                    NOT NULL,
    after_document_title      VARCHAR(20)                                    NOT NULL,
    before_parent_document_id BIGINT,
    after_parent_document_id  BIGINT,
    related_debate_id         BIGINT,
    created_at                DATETIME(6)                                    NOT NULL,
    updated_at                DATETIME(6)                                    NOT NULL,
    PRIMARY KEY (contribute_id)
);

CREATE TABLE amendment
(
    amendment_id        BIGINT                            NOT NULL auto_increment,
    contribute_id       BIGINT                            NOT NULL,
    type                ENUM ('UPDATE','DELETE','CREATE') NOT NULL,
    target_section_id   BIGINT                            NOT NULL,
    target_revision     BIGINT                            NOT NULL,
    new_section_heading ENUM ('H1','H2','H3'),
    new_section_title   VARCHAR(100),
    new_section_content TEXT,
    creating_order      INTEGER,
    created_at          DATETIME(6)                       NOT NULL,
    updated_at          DATETIME(6)                       NOT NULL,
    PRIMARY KEY (amendment_id)
);

CREATE TABLE vote
(
    vote_id       BIGINT      NOT NULL auto_increment,
    member_id     BIGINT      NOT NULL,
    contribute_id BIGINT      NOT NULL,
    agree         TINYINT,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    PRIMARY KEY (vote_id)
);

CREATE TABLE debate
(
    debate_id        BIGINT                 NOT NULL auto_increment,
    contribute_id    BIGINT                 NOT NULL,
    status           ENUM ('OPEN','CLOSED') NOT NULL,
    end_at           DATETIME(6)            NOT NULL,
    comment_sequence INTEGER                NOT NULL,
    created_at       DATETIME(6)            NOT NULL,
    updated_at       DATETIME(6)            NOT NULL,
    PRIMARY KEY (debate_id)
);

CREATE TABLE comment
(
    comment_id   BIGINT        NOT NULL auto_increment,
    debate_id    BIGINT        NOT NULL,
    commenter_id BIGINT        NOT NULL,
    content      VARCHAR(1000) NOT NULL,
    sequences    INTEGER       NOT NULL,
    created_at   DATETIME(6)   NOT NULL,
    updated_at   DATETIME(6)   NOT NULL,
    PRIMARY KEY (comment_id)
);

CREATE TABLE bookmark
(
    bookmark_id BIGINT      NOT NULL auto_increment,
    member_id   BIGINT      NOT NULL,
    document_id BIGINT      NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (bookmark_id)
);

CREATE TABLE notification
(
    notification_id BIGINT       NOT NULL auto_increment,
    message         VARCHAR(255) NOT NULL,
    uri             VARCHAR(255) NOT NULL,
    is_read         TINYINT      NOT NULL,
    member_id       BIGINT       NOT NULL,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (notification_id)
);

CREATE TABLE report
(
    report_id   BIGINT                                   NOT NULL auto_increment,
    description VARCHAR(255)                             NOT NULL,
    status      ENUM ('SUBMITTED','RESOLVED','REJECTED') NOT NULL,
    reporter_id BIGINT                                   NOT NULL,
    report_type VARCHAR(30)                              NOT NULL,
    comment_id  BIGINT,
    document_id BIGINT,
    created_at  DATETIME(6)                              NOT NULL,
    updated_at  DATETIME(6)                              NOT NULL,
    PRIMARY KEY (report_id)
);

CREATE TABLE member_badge
(
    member_id BIGINT      NOT NULL,
    badges    VARCHAR(30) NOT NULL
);

CREATE TABLE withdrawn_member
(
    withdrawn_member_id BIGINT       NOT NULL auto_increment,
    original_member_id  BIGINT       NOT NULL,
    role                VARCHAR(255) NOT NULL,
    nickname            VARCHAR(50)  NOT NULL,
    email               VARCHAR(255) NOT NULL,
    image_url           VARCHAR(255),
    social_id           VARCHAR(255),
    social_type         VARCHAR(255),
    contributes         BIGINT       NOT NULL,
    joined_at           DATETIME(6)  NOT NULL,
    withdrawn_at        datetime(6)  NOT NULL,
    PRIMARY KEY (withdrawn_member_id)
);

CREATE TABLE sequence_table
(
    sequence_name  VARCHAR(255) NOT NULL,
    sequence_value BIGINT       NOT NULL,
    PRIMARY KEY (sequence_name)
);
# fk 제약 조건 설정 - on delete 고려 필요

ALTER TABLE document
    ADD CONSTRAINT fk_document_author_id_member
        FOREIGN KEY (author_id) REFERENCES member (member_id);

ALTER TABLE document
    ADD CONSTRAINT fk_document_parent_document_id_document
        FOREIGN KEY (parent_document_id) REFERENCES document (document_id);

ALTER TABLE section
    ADD CONSTRAINT fk_section_document_id_document
        FOREIGN KEY (document_id) REFERENCES document (document_id);

ALTER TABLE contribute
    ADD CONSTRAINT fk_contribute_document_id_document
        FOREIGN KEY (document_id) REFERENCES document (document_id);

ALTER TABLE contribute
    ADD CONSTRAINT fk_contribute_before_parent_document_id_document
        FOREIGN KEY (before_parent_document_id) REFERENCES document (document_id);

ALTER TABLE contribute
    ADD CONSTRAINT fk_contribute_after_parent_document_id_document
        FOREIGN KEY (after_parent_document_id) REFERENCES document (document_id);

ALTER TABLE contribute
    ADD CONSTRAINT fk_contribute_member_id_member
        FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE contribute
    ADD CONSTRAINT fk_contribute_related_debate_id_debate
        FOREIGN KEY (related_debate_id) REFERENCES debate (debate_id);

ALTER TABLE amendment
    ADD CONSTRAINT fk_amendment_contribute_id_contribute
        FOREIGN KEY (contribute_id) REFERENCES contribute (contribute_id);

ALTER TABLE amendment
    ADD CONSTRAINT fk_amendment_target_revision_and_target_section_id_section
        FOREIGN KEY (target_revision, target_section_id) REFERENCES section (revision, section_id);

ALTER TABLE vote
    ADD CONSTRAINT fk_vote_contribute_id_contribute
        FOREIGN KEY (contribute_id) REFERENCES contribute (contribute_id);

ALTER TABLE vote
    ADD CONSTRAINT fk_vote_member_id_member
        FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE debate
    ADD CONSTRAINT fk_debate_contribute_id_contribute
        FOREIGN KEY (contribute_id) REFERENCES contribute (contribute_id);

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_commenter_id_member
        FOREIGN KEY (commenter_id) REFERENCES member (member_id);

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_debate_id_debate
        FOREIGN KEY (debate_id) REFERENCES debate (debate_id);

ALTER TABLE bookmark
    ADD CONSTRAINT fk_bookmark_document_id_document
        FOREIGN KEY (document_id) REFERENCES document (document_id);

ALTER TABLE bookmark
    ADD CONSTRAINT fk_bookmark_member_id_member
        FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE;

ALTER TABLE member_badge
    ADD CONSTRAINT fk_member_badge_member_id_member
        FOREIGN KEY (member_id) REFERENCES member (member_id);

# 유니크 제약조건 설정

ALTER TABLE member
    ADD CONSTRAINT unique_member_nickname UNIQUE (nickname);

ALTER TABLE document
    ADD CONSTRAINT unique_document_title UNIQUE (title);

ALTER TABLE bookmark
    ADD CONSTRAINT unique_bookmark_member_id_and_document_id UNIQUE (member_id, document_id);

ALTER TABLE vote
    ADD CONSTRAINT unique_vote_member_id_and_contribute_id UNIQUE (member_id, contribute_id);

# 조회 최적화를 위한 인덱스 설정

# findByVersion 에 사용 기대
ALTER TABLE section
    ADD INDEX index_document_document_id_and_revision (document_id, revision);

# findByStatusIsVotingAndCreatedAtBetween 등에 사용 기대
ALTER TABLE contribute
    ADD INDEX index_contribute_status (status);

# existsDuplicateRequestedDocumentTitle 에 사용 기대
ALTER TABLE contribute
    ADD INDEX index_contribute_after_document_title (after_document_title);

# findOpenDebateIdByEndAt 등에 사용 기대
ALTER TABLE debate
    ADD INDEX index_debate_status (status);

# getNotificationsByMemberId 에 사용 기대
ALTER TABLE notification
    ADD INDEX index_notification_member_id (member_id);