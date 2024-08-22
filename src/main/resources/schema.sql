-- Needed to only run once
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS replies;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS otps;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    roles VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    show_nsfw BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY(id)
);

INSERT INTO users(email, name, password, roles) VALUES ('rickin.shah17403@gmail.com', 'Rickin Shah', '$2a$10$w7V3R3hvUwQg2aHwBAvwX.Feok1qsfCS1sNCoMfJUorxwVJHy0pRu', 'ROLE_ADMIN');

CREATE TABLE IF NOT EXISTS otps (
    user_id UUID NOT NULL,
    otp INTEGER NOT NULL,
    expiration_time BIGINT NOT NULL,
    PRIMARY KEY(user_id),
    CONSTRAINT fk_otps_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS posts (
    id UUID DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    title VARCHAR(50) NOT NULL,
    post VARCHAR(500) NOT NULL,
    type_of_post VARCHAR(15) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    is_nsfw BOOLEAN NOT NULL,
    time TIMESTAMP NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_posts_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id UUID DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    comment VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    time TIMESTAMP NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_comments_posts
        FOREIGN KEY(post_id)
            REFERENCES posts(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_comments_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS replies (
    id UUID DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    comment_id UUID NOT NULL,
    reply VARCHAR(1500) NOT NULL,
    upvote SMALLINT NOT NULL DEFAULT 0,
    downvote SMALLINT NOT NULL DEFAULT 0,
    time TIMESTAMP NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_replies_comments
        FOREIGN KEY(comment_id)
            REFERENCES comments(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_replies_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE SET NULL
);


GRANT SELECT ON ALL TABLES IN SCHEMA public TO PUBLIC;
