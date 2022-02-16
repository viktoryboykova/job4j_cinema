CREATE TABLE account (
   id SERIAL PRIMARY KEY,
   username VARCHAR NOT NULL,
   email VARCHAR NOT NULL UNIQUE,
   phone VARCHAR NOT NULL UNIQUE
);

insert into account (id, username, email, phone) values (1, 'admin', 'admin@admin.ru', '111');

CREATE TABLE ticket (
   id SERIAL PRIMARY KEY,
   price INT NOT NULL,
   session_id INT NOT NULL,
   row INT NOT NULL,
   cell INT NOT NULL,
   available boolean,
   account_id INT NOT NULL REFERENCES account(id),
   constraint unique_ticket UNIQUE (session_id, row, cell)
);

INSERT INTO ticket (price, session_id, row, cell, available, account_id) values
 (250, 1, 1, 1, true, 1),
 (250, 1, 1, 2, true, 1),
 (250, 1, 1, 3, true, 1),
 (300, 1, 2, 1, true, 1),
 (300, 1, 2, 2, true, 1),
 (300, 1, 2, 3, true, 1),
 (350, 1, 3, 1, true, 1),
 (350, 1, 3, 2, true, 1),
 (350, 1, 3, 3, true, 1);