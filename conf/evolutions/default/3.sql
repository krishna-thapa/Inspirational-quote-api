--- !Ups

CREATE TABLE user_details_table (
    id serial PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    created_date date NOT NULL,
    is_admin boolean default true
);

-- Admin account in the database with -1 as an ID
INSERT INTO user_details_table VALUES (-1, 'admin', 'admin', 'admin@com', 'admin', '2020-10-15', true);

--- !Downs

DROP TABLE if exists user_details_table CASCADE;