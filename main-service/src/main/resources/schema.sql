DROP TABLE IF EXISTS categories;

CREATE TABLE IF NOT EXISTS categories (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL --возможно, 50 не включительно
);