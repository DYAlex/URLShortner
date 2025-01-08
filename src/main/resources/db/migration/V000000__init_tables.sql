CREATE TABLE users
(
    uuid UUID PRIMARY KEY
);

CREATE TABLE urls
(
    id             SERIAL PRIMARY KEY,
    user_uuid      UUID                        NOT NULL,
    full_url       varchar                     NOT NULL,
    short_url      varchar                     NOT NULL,
    created_at     timestamp without time zone NOT NULL,
    ttl            bigint,
    max_follows    bigint,
    follow_counter bigint,
    CONSTRAINT fk_users FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);
