CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR NOT NULL,
                         email VARCHAR NOT NULL UNIQUE,
                         phone VARCHAR NOT NULL UNIQUE
);
insert into account (username, email, phone) VALUES (1,1,1);

CREATE TABLE ticket (
                        id SERIAL PRIMARY KEY,
                        film_id INT NOT NULL,
                        session_id INT NOT NULL,
                        row INT NOT NULL,
                        cell INT NOT NULL,
                        account_id INT NOT NULL REFERENCES account(id),
                        CONSTRAINT art unique (film_id, row, cell, session_id)
);
