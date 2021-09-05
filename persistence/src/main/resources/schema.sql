CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(250),
    email VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS community(
    community_id SERIAL PRIMARY KEY,
    name VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS  forum(
    forum_id SERIAL PRIMARY KEY,
    name VARCHAR(250),
    community_id INT,
    FOREIGN KEY (community_id) REFERENCES community
);

CREATE TABLE IF NOT EXISTS question (
    question_id SERIAL PRIMARY KEY,
    title VARCHAR(250),
    body TEXT,
    user_id INT,
        FOREIGN KEY (user_id) REFERENCES users,
    forum_id INT,
        FOREIGN KEY (forum_id) REFERENCES forum,
    time TIMESTAMP NOT NULL DEFAULT(current_timestamp)
);


