CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER IDENTITY PRIMARY KEY,
    username VARCHAR(250),
    email VARCHAR(250) UNIQUE,
    password VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS community(
    community_id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(250) UNIQUE, --TODO: Renombrar nombres repetidos en producción
    description LONGVARCHAR,
    moderator_id INT,
    FOREIGN KEY (moderator_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS  forum(
    forum_id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(250),
    community_id INT,
    FOREIGN KEY (community_id) REFERENCES community
);



CREATE TABLE IF NOT EXISTS question (
    question_id INTEGER IDENTITY PRIMARY KEY,
    title VARCHAR(250),
    body LONGVARCHAR,
    user_id INT,
        FOREIGN KEY (user_id) REFERENCES users,
    forum_id INT,
        FOREIGN KEY (forum_id) REFERENCES forum,
    time TIMESTAMP DEFAULT NOW() NOT NULL,
    image_id INT,
        FOREIGN KEY (image_id) references images
);

CREATE TABLE IF NOT EXISTS answer(
    answer_id INTEGER IDENTITY primary key,
    body LONGVARCHAR not null,
    verify boolean,
    question_id int not null,
    foreign key (question_id) references question,
    user_id int not null,
    time TIMESTAMP DEFAULT NOW() NOT NULL,
    foreign key (user_id) references users
);

CREATE TABLE IF NOT EXISTS answerVotes(
    votes_id INTEGER IDENTITY primary key,
    vote boolean,
    answer_id INT,
    foreign key (answer_id) references answer,
    user_id INT,
    foreign key (user_id) references users
    );

CREATE TABLE IF NOT EXISTS questionVotes(
    votes_id INTEGER IDENTITY primary key,
    vote boolean,
    question_id INT,
    foreign key (question_id) references question,
    user_id INT,
    foreign key (user_id) references users
);

CREATE TABLE IF NOT EXISTS access( /*TODO: modificar la BD actual en el deploy*/
    access_id INTEGER IDENTITY PRIMARY KEY,
    community_id INT,
    user_id INT,
    access_type INT,
    FOREIGN KEY (community_id) REFERENCES community ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    UNIQUE (community_id,user_id)
);