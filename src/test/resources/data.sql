INSERT INTO author (id, name)
VALUES (101, 'Jan Klaassen'),
       (102, 'Rene Goscinny');

INSERT INTO book (id, isbn, title, author_id, publisher, number_of_copies)
VALUES (201, 'isbn123', 'De poppenkast deel 1', 101, 'De Uitgeverij', 1),
       (202, 'isbn124', 'De poppenkast deel 2', 101, 'De Uitgeverij', 2),
       (203, 'isbn444', 'Asterix & Oblix',      102, 'Dargaud', 3);

INSERT INTO users (username, password, enabled)
VALUES ('admin', 'pwadmin', true),
       ('user101', 'pwuser', true),
       ('user102', 'pwuser', true);

-- NB @PreAuthorize("hasRole('USER')") automatically prefixes 'USER' with 'ROLE_'
INSERT INTO authorities (id, authority, username)
VALUES (500, 'ROLE_ADMIN', 'admin'),
       (501, 'ROLE_USER', 'admin'),
       (502, 'ROLE_USER', 'user101'),
       (503, 'ROLE_USER', 'user102');

INSERT INTO checkout (id, username, book_id, checkout_at, returned)
VALUES (401, 'user101', 201, '2025-07-08', false),
       (402, 'user101', 202, '2025-07-15', false),
       (403, 'user101', 203, '2025-03-04', true);
