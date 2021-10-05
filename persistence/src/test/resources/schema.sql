CREATE TABLE IF NOT EXISTS users (
                                     user_id INT IDENTITY PRIMARY KEY,
                                     username VARCHAR(250),
                                     email VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS community(
                                        community_id INT IDENTITY PRIMARY KEY,
                                        name VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS  forum(
                                     forum_id INT IDENTITY PRIMARY KEY,
                                     name VARCHAR(250),
                                     community_id INT,
                                     FOREIGN KEY (community_id) REFERENCES community
);

CREATE TABLE IF NOT EXISTS question (
                                        question_id INT IDENTITY PRIMARY KEY,
                                        title VARCHAR(250),
                                        body LONGVARCHAR,
                                        user_id INT,
                                        FOREIGN KEY (user_id) REFERENCES users,
                                        forum_id INT,
                                        FOREIGN KEY (forum_id) REFERENCES forum,
                                        time TIMESTAMP DEFAULT NOW() NOT NULL
);




