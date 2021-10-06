create table if not exists images(
                                     image_id SERIAL NOT NULL PRIMARY KEY ,
                                     image bytea NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(250),
    email VARCHAR(250) UNIQUE,
    password VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS community(
    community_id SERIAL PRIMARY KEY,
    name VARCHAR(250) UNIQUE, --TODO: Renombrar nombres repetidos en producción
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
    time TIMESTAMP NOT NULL DEFAULT(current_timestamp),
    image_id INT,
        FOREIGN KEY (image_id) references images
);

CREATE TABLE IF NOT EXISTS answer(
    answer_id serial primary key,
    body text not null,
    verify boolean,
    question_id int not null,
    foreign key (question_id) references question,
    user_id int not null,
    time TIMESTAMP NOT NULL DEFAULT(current_timestamp),
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

CREATE TABLE IF NOT EXISTS questionVotes(
    votes_id serial primary key,
    vote boolean,
    question_id INT,
    foreign key (question_id) references question,
    user_id INT,
    foreign key (user_id) references users
);

CREATE TABLE IF NOT EXISTS access(
    access_id SERIAL PRIMARY KEY,
    community_id INT,
    user_id INT,
    access_type INT,
    FOREIGN KEY (community_id) REFERENCES community ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    UNIQUE (community_id,user_id)
);


create or replace view answer_votes_summary as select answer_id , count(vote) as total_votes
                                                    , sum( case when vote = true then 1
                                                                when vote = false then -1
                                                                else 0
        end) as vote_sum  from answervotes group by answer_id;


create or replace view question_votes_summary as
SELECT questionvotes.question_id,
       count(questionvotes.vote) AS total_votes,
       sum( CASE
                WHEN questionvotes.vote = true THEN 1
                WHEN questionvotes.vote = false THEN -1
                ELSE 0
           END)        AS vote_sum
FROM questionvotes
GROUP BY questionvotes.question_id;
select * from (select
                   user_id , sum(question_votes_summary.vote_sum) as q_karma
               from question natural join question_votes_summary
               group by user_id)
                  as question_karma
                  full outer join (select
                                       user_id , sum(answer_votes_summary.vote_sum) as a_karma
                                   from answer natural join answer_votes_summary
                                   group by user_id)
    as answer_karma
                                  on question_karma.user_id = answer_karma.user_id;

create or replace view karma as
select   users.user_id , (COALESCE(question_karma.q_karma, 0) +COALESCE(answer_karma.a_karma, 0)) as karma
from  users left outer
                join( (select
                           user_id , sum(question_votes_summary.vote_sum) as q_karma
                       from question natural join question_votes_summary
                       group by user_id)
    as question_karma
    full outer join (select
                         user_id , sum(answer_votes_summary.vote_sum) as a_karma
                     from answer natural join answer_votes_summary
                     group by user_id)
        as answer_karma
    on question_karma.user_id = answer_karma.user_id)
                    on (users.user_id = answer_karma.user_id or users.user_id = question_karma.user_id);
create or replace view full_answers as Select coalesce(votes,0) as votes ,answer.answer_id, body, coalesce(verify,false) as verify, question_id, time ,  users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password
                                       from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes
                                                                                                           from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id

