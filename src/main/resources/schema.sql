-- Needed to only run once
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS otps;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    roles VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    nsfw BOOLEAN NOT NULL DEFAULT FALSE,
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

CREATE TABLE IF NOT EXISTS questions (
    id UUID DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    title VARCHAR(50) NOT NULL,
    query VARCHAR(500) NOT NULL,
    media VARCHAR(50) DEFAULT NULL,
    media_type VARCHAR(10) DEFAULT NULL,
    posted_date DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY(id),
    CONSTRAINT fk_queries_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS answers (
    id SMALLINT GENERATED ALWAYS AS IDENTITY,
    user_id UUID NOT NULL,
    question_id UUID NOT NULL,
    vote SMALLINT NOT NULL DEFAULT 0,
    answer VARCHAR(1500) NOT NULL,
    posted_date DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY(id),
    CONSTRAINT fk_answers_queries
        FOREIGN KEY(question_id)
            REFERENCES questions(id)
                ON DELETE CASCADE,
    CONSTRAINT fk_answers_users
        FOREIGN KEY(user_id)
            REFERENCES users(id)
                ON DELETE SET NULL
);


GRANT SELECT ON ALL TABLES IN SCHEMA public TO PUBLIC;
