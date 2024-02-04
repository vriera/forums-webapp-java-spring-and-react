-- dummy data for testing

-- create 3 dummy users
insert into users (user_id, username, email, password) values (1, 'User 1', 'user1@test.com', '123456');
insert into users (user_id, username, email, password) values (2, 'User 2', 'user2@test.com', '223456');
insert into users (user_id, username, email, password) values (3, 'User 3', 'user3@test.com', '323456');

-- create 3 dummy communities
insert into community (community_id, name, description, moderator_id) values (1, 'Community 1', 'Description 1', 1);
insert into community (community_id, name, description, moderator_id) values (2, 'Community 2', 'Description 2', 2);
insert into community (community_id, name, description, moderator_id) values (3, 'Community 3', 'Description 3', 3);

-- create 3 dummy forums
insert into forum (forum_id, name, community_id) values (1, 'Forum 1', 1);
insert into forum (forum_id, name, community_id) values (2, 'Forum 2', 2);
insert into forum (forum_id, name, community_id) values (3, 'Forum 3', 3);

-- create 6 dummy questions
insert into question (question_id, title, body, user_id, forum_id, time) values (1, 'Question 1', 'Body 1', 1, 1, '2021-01-01 00:00:00');
insert into question (question_id, title, body, user_id, forum_id, time) values (2, 'Question 2', 'Body 2', 1, 1, '2021-01-01 00:00:00');
insert into question (question_id, title, body, user_id, forum_id, time) values (3, 'Question 3', 'Body 3', 3, 3, '2021-01-01 00:00:00');
insert into question (question_id, title, body, user_id, forum_id, time) values (4, 'Question 4', 'Body 4', 1, 1, '2021-01-01 00:00:00');
insert into question (question_id, title, body, user_id, forum_id, time) values (5, 'Question 5', 'Body 5', 2, 2, '2021-01-01 00:00:00');
insert into question (question_id, title, body, user_id, forum_id, time) values (6, 'Question 6', 'Body 6', 3, 3, '2021-01-01 00:00:00');

-- create 4 dummy answers
insert into answer (answer_id, body, verify, question_id, user_id, time) values (1, 'Answer 1', true, 1, 1, '2021-01-01 00:00:00');
insert into answer (answer_id, body, verify, question_id, user_id, time) values (2, 'Answer 2', false, 1, 3, '2021-01-01 00:00:00');
insert into answer (answer_id, body, verify, question_id, user_id, time) values (3, 'Answer 3', true, 2, 3, '2021-01-01 00:00:00');
insert into answer (answer_id, body, verify, question_id, user_id, time) values (4, 'Answer 4', true, 3, 1, '2021-01-01 00:00:00');

