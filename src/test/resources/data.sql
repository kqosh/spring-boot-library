INSERT INTO author (id, name)
VALUES (101, 'Jan Klaassen'),
       (102, 'Rene Goscinny'),
       (103, 'Katrijn Klaassen'),
       (104, 'Klaas Jansen'),
       (105, 'Kess');

INSERT INTO book (id, isbn, title, author_id, publisher, number_of_copies)
VALUES (201, 'isbn123', 'De poppenkast deel 1', 101, 'De Uitgeverij', 1),
       (202, 'isbn124', 'De poppenkast deel 2', 101, 'De Uitgeverij', 2),
       (203, 'isbn444', 'Asterix & Oblix',      102, 'Dargaud', 3),
       (204, 'isbn125', 'De poppenkast deel 3', 103, 'De Uitgeverij', 0);

INSERT INTO users (username, password, enabled, loan_period_in_days, max_renew_count)
VALUES ('admin', 'pwadmin', true, 365, 10),
       ('user101', 'pwuser', true, 21, 1),
       ('user102', 'pwuser', true, 21, 1);

-- NB @PreAuthorize("hasRole('USER')") automatically prefixes 'USER' with 'ROLE_'
INSERT INTO authorities (id, authority, username)
VALUES (500, 'ROLE_ADMIN', 'admin'),
       (501, 'ROLE_USER', 'admin'),
       (502, 'ROLE_USER', 'user101'),
       (503, 'ROLE_USER', 'user102');

INSERT INTO checkout (id, username, book_id, checkout_at, due_date, renew_count, returned)
VALUES (401, 'user101', 201, '2025-07-08T12:00:00+02:00', '2025-07-29T12:00:00+02:00', 0, false),
       (402, 'user101', 202, '2025-07-15T12:00:00+02:00', '2025-08-05T12:00:00+02:00', 0, false),
       (403, 'user101', 203, '2025-03-04T12:00:00+02:00', '2025-03-25T12:00:00+02:00', 0, true);
