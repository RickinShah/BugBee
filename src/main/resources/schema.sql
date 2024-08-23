-- Needed to only run once
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS bugbee;

CREATE SEQUENCE IF NOT EXISTS bugbee.table_id_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE FUNCTION bugbee.next_id(OUT result bigint) AS '
DECLARE
    our_epoch bigint := 1314220021721;
    seq_id bigint;
    now_millis bigint;
    shard_id int := 5;
BEGIN
    SELECT MOD(nextval(''bugbee.table_id_seq''), 1024) INTO seq_id;  SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
    result := (now_millis - our_epoch) << 23;
    result := result | (shard_id <<10);
    result := result | (seq_id);
END;
' LANGUAGE PLPGSQL;


-- ORIGINAL CODE
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


DROP TABLE IF EXISTS bugbee.post_votes;
DROP TABLE IF EXISTS bugbee.replies;
DROP TABLE IF EXISTS bugbee.comments;
DROP TABLE IF EXISTS bugbee.posts;
DROP TABLE IF EXISTS bugbee.otps;
DROP TABLE IF EXISTS bugbee.users;

CREATE TABLE IF NOT EXISTS bugbee.users (
    id BIGINT DEFAULT bugbee.next_id(),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    roles VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    show_nsfw BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY(id)
);

INSERT INTO bugbee.users(email, name, password, roles) VALUES ('rickin.shah17403@gmail.com', 'Rickin Shah', '$2a$10$w7V3R3hvUwQg2aHwBAvwX.Feok1qsfCS1sNCoMfJUorxwVJHy0pRu', 'ROLE_ADMIN');

CREATE TABLE IF NOT EXISTS bugbee.otps (
    user_id BIGINT DEFAULT bugbee.next_id(),
    otp INTEGER NOT NULL,
    expiration_time BIGINT NOT NULL,
    PRIMARY KEY(user_id),
    CONSTRAINT fk_otps_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bugbee.posts (
    id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    title VARCHAR(50) NOT NULL,
    post VARCHAR(500) NOT NULL,
    type_of_post VARCHAR(15) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    is_nsfw BOOLEAN NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_posts_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.comments (
    id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    post_id BIGINT NOT NULL,
    comment VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    date DATE NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_comments_posts
        FOREIGN KEY(post_id)
            REFERENCES bugbee.posts(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_comments_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.replies (
    id BIGINT DEFAULT bugbee.next_id(),
    user_id BIGINT,
    comment_id BIGINT NOT NULL,
    reply VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    date DATE NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_replies_comments
        FOREIGN KEY(comment_id)
            REFERENCES bugbee.comments(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_replies_users
        FOREIGN KEY(user_id)
            REFERENCES bugbee.users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bugbee.post_votes (
    id BIGINT DEFAULT bugbee.next_id(),
    post_id BIGINT,
    user_id BIGINT,
    type_of_vote BOOLEAN NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_post_votes_posts
        FOREIGN KEY(post_id)
            REFERENCES bugbee.posts(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_post_votes_users
        FOREIGN KEY (user_id)
            REFERENCES bugbee.users(id)
                ON DELETE SET NULL
);

GRANT SELECT ON ALL TABLES IN SCHEMA bugbee TO PUBLIC;
