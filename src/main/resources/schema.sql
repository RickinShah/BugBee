CREATE SCHEMA IF NOT EXISTS bugbee;

CREATE SEQUENCE IF NOT EXISTS bugbee.table_id_seq START WITH 1 INCREMENT BY 1;

-- NEEDED TO ONLY RUN ONCE
CREATE OR REPLACE FUNCTION bugbee.next_id(OUT result bigint) AS
'
    DECLARE
        our_epoch  bigint := 1314220021721;
        seq_id     bigint;
        now_millis bigint;
        shard_id   int    := 5;
    BEGIN
        SELECT MOD(nextval(''bugbee.table_id_seq''), 1024)
        INTO seq_id;
        SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000)
        INTO now_millis;
        result := (now_millis - our_epoch) << 23;
        result := result | (shard_id << 10);
        result := result | (seq_id);
    END;
' LANGUAGE PLPGSQL;


-- ORIGINAL CODE (if you want to add function directly to postgresql db)
-- CREATE OR REPLACE FUNCTION bugbee.next_id(OUT result bigint) AS $$
--     DECLARE
--         our_epoch bigint := 1314220021721;
--         seq_id bigint;
--         now_millis bigint;
--         shard_id int := 5;
--     BEGIN
--         SELECT MOD(nextval('bugbee.table_id_seq'), 1024) INTO seq_id;  SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
--         result := (now_millis - our_epoch) << 23;
--         result := result | (shard_id <<10);
--         result := result | (seq_id);
--     END;
-- $$ LANGUAGE PLPGSQL;


-- Don't change the sequence of these tables
-- Uncomment below if you want functionality like DROP-CREATE
-- DROP TABLE IF EXISTS bugbee.reply_user_vote;
-- DROP TABLE IF EXISTS bugbee.comment_user_vote;
-- DROP TABLE IF EXISTS bugbee.post_user_vote;
-- DROP TABLE IF EXISTS bugbee.replies;
-- DROP TABLE IF EXISTS bugbee.comments;
DROP TABLE IF EXISTS bugbee.resources;
-- DROP TABLE IF EXISTS bugbee.posts;
-- DROP TABLE IF EXISTS bugbee.otps;
-- DROP TABLE IF EXISTS bugbee.users;

CREATE TABLE IF NOT EXISTS bugbee.users
(
    user_pid  BIGINT                       DEFAULT bugbee.next_id(),
    username  VARCHAR(50) UNIQUE  NOT NULL,
    email     VARCHAR(255) UNIQUE NOT NULL,
    name      VARCHAR(50),
    password  VARCHAR(64)         NOT NULL,
    roles     VARCHAR(15)         NOT NULL DEFAULT 'ROLE_USER',
    show_nsfw BOOLEAN             NOT NULL DEFAULT FALSE,
    profile   VARCHAR(15)         NOT NULL DEFAULT 'P1',
    bio       VARCHAR(100),
    PRIMARY KEY (user_pid)
);

-- Adding a user as ROLE_ADMIN
-- Username: admin
-- Password: admin
-- INSERT INTO bugbee.users(email, username, name, password, roles, profile) VALUES (
--     'rickinshah.21.cs@iite.indusuni.ac.in',
--     'admin',
--     'Admin',
--     '$2a$10$QKy1jx.1gw9Ud5qRyc8PJeXIsJzhm0HkudjiC6JKSsR0UCvCQW7jS',
--     'ROLE_ADMIN',
--     'P1');

CREATE TABLE IF NOT EXISTS bugbee.otps
(
    user_pid        BIGINT DEFAULT bugbee.next_id(),
    otp             INTEGER NOT NULL,
    expiration_time BIGINT  NOT NULL,
    PRIMARY KEY (user_pid),
    CONSTRAINT fk_otps_users
        FOREIGN KEY (user_pid)
            REFERENCES bugbee.users (user_pid)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bugbee.posts
(
    post_pid       BIGINT                DEFAULT bugbee.next_id(),
    user_id        BIGINT,
    title          VARCHAR(50)  NOT NULL,
    content        VARCHAR(500) NOT NULL,
    post_type      VARCHAR(15)  NOT NULL,
    upvote_count   INT          NOT NULL DEFAULT 0,
    downvote_count INT          NOT NULL DEFAULT 0,
    comment_count  INT          NOT NULL DEFAULT 0,
    updated_at     DATE         NOT NULL,
    update_flag    BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (post_pid),
    CONSTRAINT fk_posts_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.resources
(
    post_pid      BIGINT,
    file_format  VARCHAR(15)  NOT NULL,
    nsfw_flag    BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (post_pid),
    CONSTRAINT fk_resources_posts
        FOREIGN KEY (post_pid)
            REFERENCES bugbee.posts (post_pid)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bugbee.comments
(
    comment_pid    BIGINT                 DEFAULT bugbee.next_id(),
    user_id        BIGINT,
    post_id        BIGINT        NOT NULL,
    content        VARCHAR(1500) NOT NULL,
    upvote_count   INT           NOT NULL DEFAULT 0,
    downvote_count INT           NOT NULL DEFAULT 0,
    reply_count    INT           NOT NULL DEFAULT 0,
    updated_at     DATE          NOT NULL,
    update_flag    BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (comment_pid),
    CONSTRAINT fk_comments_posts
        FOREIGN KEY (post_id)
            REFERENCES bugbee.posts (post_pid)
            ON DELETE CASCADE,
    CONSTRAINT fk_comments_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.replies
(
    reply_pid      BIGINT                 DEFAULT bugbee.next_id(),
    user_id        BIGINT,
    comment_id     BIGINT        NOT NULL,
    content        VARCHAR(1500) NOT NULL,
    upvote_count   INT           NOT NULL DEFAULT 0,
    downvote_count INT           NOT NULL DEFAULT 0,
    updated_at     DATE          NOT NULL,
    update_flag    BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (reply_pid),
    CONSTRAINT fk_replies_comments
        FOREIGN KEY (comment_id)
            REFERENCES bugbee.comments (comment_pid)
            ON DELETE CASCADE,
    CONSTRAINT fk_replies_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.post_user_vote
(
    vote_pid    BIGINT DEFAULT bugbee.next_id(),
    post_id     BIGINT,
    user_id     BIGINT,
    vote_status BOOLEAN NOT NULL,
    PRIMARY KEY (vote_pid),
    CONSTRAINT fk_post_user_vote_posts
        FOREIGN KEY (post_id)
            REFERENCES bugbee.posts (post_pid)
            ON DELETE CASCADE,
    CONSTRAINT fk_post_user_vote_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.comment_user_vote
(
    vote_pid    BIGINT DEFAULT bugbee.next_id(),
    comment_id  BIGINT,
    user_id     BIGINT,
    vote_status BOOLEAN NOT NULL,
    PRIMARY KEY (vote_pid),
    CONSTRAINT fk_comment_user_vote_comments
        FOREIGN KEY (comment_id)
            REFERENCES bugbee.comments (comment_pid)
            ON DELETE CASCADE,
    CONSTRAINT fk_comment_user_vote_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.reply_user_vote
(
    vote_pid    BIGINT DEFAULT bugbee.next_id(),
    reply_id    BIGINT,
    user_id     BIGINT,
    vote_status BOOLEAN NOT NULL,
    PRIMARY KEY (vote_pid),
    CONSTRAINT fk_reply_user_vote_replies
        FOREIGN KEY (reply_id)
            REFERENCES bugbee.replies (reply_pid)
            ON DELETE CASCADE,
    CONSTRAINT fk_reply_user_vote_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users (user_pid)
            ON DELETE SET NULL
);

GRANT SELECT ON ALL TABLES IN SCHEMA bugbee TO PUBLIC;
