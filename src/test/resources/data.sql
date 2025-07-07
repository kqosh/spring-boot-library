INSERT INTO author (id, name)
VALUES (101, 'Jan Klaassen');

INSERT INTO book (id, isbn, title, author_id, publisher, number_of_copies)
VALUES (101, 'isbn123', 'De poppenkast deel 1', 101, 'De Uitgeverij', 8),
       (102, 'isbn124', 'De poppenkast deel 2', 101, 'De Uitgeverij', 9);