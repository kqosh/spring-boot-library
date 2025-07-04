INSERT INTO author (id, name)
VALUES (101, 'Jan Klaassen');

INSERT INTO book (id, isbn, title, author_id, publisher)
VALUES (101, 'isbn123', 'De poppenkast deel 1', 101, 'De Uitgeverij'),
       (102, 'isbn124', 'De poppenkast deel 2', 101, 'De Uitgeverij');