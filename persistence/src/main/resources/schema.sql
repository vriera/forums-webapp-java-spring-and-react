CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username varchar(250),
    email varchar(250)
);

CREATE TABLE IF NOT EXISTS community(
    id SERIAL PRIMARY KEY,
    name varchar(250)
);


CREATE TABLE IF NOT EXISTS question (
    id SERIAL primary key,
    body TEXT,
    title varchar(250),
    user_id int,
        FOREIGN KEY (user_id) REFERENCES users(id),
    community_id int,
        FOREIGN KEY (community_id) REFERENCES community(id),
    time TIMESTAMP not null default(current_timestamp)
);