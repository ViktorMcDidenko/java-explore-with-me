DROP TABLE IF EXISTS categories, users, events, compilations, compilation_event;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL --возможно, 50 не включительно
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    requests INTEGER,
    created TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator BIGINT REFERENCES users (id) ON DELETE CASCADE,
    lat FLOAT,
    lon FLOAT,
    paid BOOLEAN,
    participant_limit INTEGER,
    published TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state VARCHAR(9),
    title VARCHAR(120) NOT NULL,
    views BIGINT
);

CREATE TABLE IF NOT EXISTS compilations (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id INTEGER NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY(compilation_id, event_id)
);