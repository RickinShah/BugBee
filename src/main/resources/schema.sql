CREATE SCHEMA IF NOT EXISTS bugbee;

CREATE SEQUENCE IF NOT EXISTS bugbee.table_id_seq START WITH 1 INCREMENT BY 1;

-- NEEDED TO ONLY RUN ONCE
-- CREATE OR REPLACE FUNCTION bugbee.next_id(OUT result bigint) AS '
-- DECLARE
--     our_epoch bigint := 1314220021721;
--     seq_id bigint;
--     now_millis bigint;
--     shard_id int := 5;
-- BEGIN
--     SELECT MOD(nextval(''bugbee.table_id_seq''), 1024) INTO seq_id;  SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
--     result := (now_millis - our_epoch) << 23;
--     result := result | (shard_id <<10);
--     result := result | (seq_id);
-- END;
-- ' LANGUAGE PLPGSQL;


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
-- DROP TABLE IF EXISTS bugbee.reply_votes;
-- DROP TABLE IF EXISTS bugbee.comment_votes;
-- DROP TABLE IF EXISTS bugbee.post_votes;
-- DROP TABLE IF EXISTS bugbee.replies;
-- DROP TABLE IF EXISTS bugbee.comments;
-- DROP TABLE IF EXISTS bugbee.posts;
-- DROP TABLE IF EXISTS bugbee.otps;
-- DROP TABLE IF EXISTS bugbee.users;


CREATE TABLE IF NOT EXISTS bugbee.users (
    user_id BIGINT DEFAULT bugbee.next_id(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(64) NOT NULL,
    roles VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    show_nsfw BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY(user_id)
);

-- Adding a user as ROLE_ADMIN
-- Username: admin
-- Password: admin
-- INSERT INTO bugbee.users(email, username, name, password, roles) VALUES (
--     'rickinshah.21.cs@iite.indusuni.ac.in',
--     'admin',
--     'Admin',
--     '$2a$10$QKy1jx.1gw9Ud5qRyc8PJeXIsJzhm0HkudjiC6JKSsR0UCvCQW7jS',
--     'ROLE_ADMIN');

CREATE TABLE IF NOT EXISTS bugbee.otps (
    user_id BIGINT DEFAULT bugbee.next_id(),
    otp INTEGER NOT NULL,
    expiration_time BIGINT NOT NULL,
    PRIMARY KEY(user_id),
    CONSTRAINT fk_otps_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bugbee.posts (
    post_id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    title VARCHAR(50) NOT NULL,
    content VARCHAR(500) NOT NULL,
    type_of_post VARCHAR(15) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    total_comments SMALLINT NOT NULL DEFAULT 0,
    nsfw BOOLEAN NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY(post_id),
    CONSTRAINT fk_posts_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.comments (
    comment_id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    post_id BIGINT NOT NULL,
    comment VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    date DATE NOT NULL,
    PRIMARY KEY(comment_id),
    CONSTRAINT fk_comments_posts
        FOREIGN KEY(post_id)
            REFERENCES bugbee.posts(post_id)
                ON DELETE CASCADE,
    CONSTRAINT fk_comments_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.replies (
    reply_id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    comment_id BIGINT NOT NULL,
    reply VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    date DATE NOT NULL,
    PRIMARY KEY(reply_id),
    CONSTRAINT fk_replies_comments
        FOREIGN KEY(comment_id)
            REFERENCES bugbee.comments(comment_id)
                ON DELETE CASCADE,
    CONSTRAINT fk_replies_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.post_votes (
    post_vote_id BIGINT DEFAULT bugbee.next_id(),
    post_id BIGINT,
    user_id BIGINT,
    upvote BOOLEAN NOT NULL,
    PRIMARY KEY(post_vote_id),
    CONSTRAINT fk_post_votes_posts
        FOREIGN KEY(post_id)
            REFERENCES bugbee.posts(post_id)
                ON DELETE CASCADE,
    CONSTRAINT fk_post_votes_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.comment_votes (
    comment_vote_id BIGINT DEFAULT bugbee.next_id(),
    comment_id BIGINT,
    user_id BIGINT,
    upvote BOOLEAN NOT NULL,
    PRIMARY KEY (comment_vote_id),
    CONSTRAINT fk_comment_votes_comments
        FOREIGN KEY (comment_id)
            REFERENCES bugbee.comments(comment_id)
                ON DELETE CASCADE,
    CONSTRAINT fk_comment_votes_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.reply_votes (
    reply_vote_id BIGINT DEFAULT bugbee.next_id(),
    reply_id BIGINT,
    user_id BIGINT,
    upvote BOOLEAN NOT NULL,
    PRIMARY KEY (reply_vote_id),
    CONSTRAINT fk_reply_votes_replies
        FOREIGN KEY (reply_id)
            REFERENCES bugbee.replies(reply_id)
                ON DELETE CASCADE,
    CONSTRAINT fk_reply_votes_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users(user_id)
                ON DELETE SET NULL
);

GRANT SELECT ON ALL TABLES IN SCHEMA bugbee TO PUBLIC;
