CREATE TABLE users (
    id bigint PRIMARY KEY ,
    username varchar NOT NULL,
    password varchar NOT NULL,
    salt varchar NOT NULL,
    alphabet varchar NOT NULL
);

CREATE TABLE urls (
    id bigint PRIMARY KEY,
    user_id bigint NOT NULL,
    full_url varchar NOT NULL,
    short_url varchar NOT NULL,
    created_at timestamp without time zone NOT NULL,
    ttl bigint,
    max_follows bigint,
    follow_counter bigint,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(id)
);
