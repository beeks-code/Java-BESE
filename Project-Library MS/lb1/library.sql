-- Run this in MySQL before starting the server

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS books;

-- 10 fixed books in the library
CREATE TABLE books (
    book_id   INT PRIMARY KEY,
    book_name VARCHAR(100),
    available BOOLEAN DEFAULT TRUE
);

INSERT INTO books VALUES (1,  'Java Programming',         TRUE);
INSERT INTO books VALUES (2,  'Data Structures',          TRUE);
INSERT INTO books VALUES (3,  'Operating Systems',        TRUE);
INSERT INTO books VALUES (4,  'Database Management',      TRUE);
INSERT INTO books VALUES (5,  'Computer Networks',        TRUE);
INSERT INTO books VALUES (6,  'Discrete Mathematics',     TRUE);
INSERT INTO books VALUES (7,  'Software Engineering',     TRUE);
INSERT INTO books VALUES (8,  'Artificial Intelligence',  TRUE);
INSERT INTO books VALUES (9,  'Web Technologies',         TRUE);
INSERT INTO books VALUES (10, 'Python Programming',       TRUE);

-- Students table
CREATE TABLE students (
    student_id   INT PRIMARY KEY,
    student_name VARCHAR(100),
    book_id      INT DEFAULT NULL,
    issue_date   DATE DEFAULT NULL,
    return_date  DATE DEFAULT NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);
