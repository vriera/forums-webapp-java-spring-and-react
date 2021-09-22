CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(250),
    email VARCHAR(250) UNIQUE,
    password VARCHAR(250) /*TODO: AGREGAR A PRODUCCIÓN*/
);

CREATE TABLE IF NOT EXISTS community(
    community_id SERIAL PRIMARY KEY,
    name VARCHAR(250),
    description TEXT,
    moderator_id INT,
    FOREIGN KEY (moderator_id) REFERENCES users
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

CREATE TABLE IF NOT EXISTS answer(
    answer_id serial primary key,
    body text not null,
    verify boolean,
    question_id int not null,
    foreign key (question_id) references question,
    user_id int not null,
    foreign key (user_id) references users
);

CREATE TABLE IF NOT EXISTS answerVotes(
    votes_id serial primary key,
    vote boolean,
    answer_id INT,
    foreign key (answer_id) references answer,
    user_id INT,
    foreign key (user_id) references users
    );
-- TODO: correr el script para agregar columna de descripcion a community
--ALTER TABLE community ADD COLUMN IF NOT EXISTS description text;
--ALTER TABLE community ADD COLUMN IF NOT EXISTS moderator_id int;

CREATE TABLE IF NOT EXISTS questionVotes(
    votes_id serial primary key,
    vote boolean,
    question_id INT,
    foreign key (question_id) references question,
    user_id INT,
    foreign key (user_id) references users
);



