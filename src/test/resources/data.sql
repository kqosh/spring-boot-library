INSERT INTO author (id, name)
VALUES (101, 'Jan Klaassen'),
       (102, 'Rene Goscinny');

INSERT INTO book (id, isbn, title, author_id, publisher, number_of_copies)
VALUES (201, 'isbn123', 'De poppenkast deel 1', 101, 'De Uitgeverij', 8),
       (202, 'isbn124', 'De poppenkast deel 2', 101, 'De Uitgeverij', 9),
       (203, 'isbn444', 'Asterix & Oblix',      102, 'Dargaud', 12);

INSERT INTO member (id, number, name)
VALUES (301, 'nr101', 'name101'),
       (302, 'nr102', 'name103');

INSERT INTO users (username, password, enabled)
VALUES ('admin', 'pwadmin', true),
       ('nr101', 'pwuser', true),
       ('nr102', 'pwuser', true);

-- NB @PreAuthorize("hasRole('USER')") automatically prefixes 'USER' with 'ROLE_'
INSERT INTO authorities (id, authority, username)
VALUES (500, 'ROLE_ADMIN', 'admin'),
       (501, 'ROLE_USER', 'admin'),
       (502, 'ROLE_USER', 'nr101'),
       (503, 'ROLE_USER', 'nr102');

INSERT INTO checkout (id, member_id, book_id, checkout_at, returned)
VALUES (401, 301, 201, '2025-07-08', false),
       (402, 301, 202, '2025-07-15', false),
       (403, 301, 203, '2025-03-04', true);
