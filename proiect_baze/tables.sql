CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    genre_id INTEGER REFERENCES genres(id) ON DELETE SET NULL --dacă rândul referențiat din tabela genres este șters, valoarea coloanei genre_id din tabela books va fi setată la NULL.
);

CREATE OR REPLACE PROCEDURE add_book(
    IN book_title VARCHAR,
    IN book_author VARCHAR,
    IN genre_name VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    genre_id INTEGER;
BEGIN

    SELECT id INTO genre_id FROM genres WHERE name = genre_name;
    IF genre_id IS NULL THEN
        INSERT INTO genres (name) VALUES (genre_name) RETURNING id INTO genre_id;
    END IF;


    INSERT INTO books (title, author, genre_id) VALUES (book_title, book_author, genre_id);

    COMMIT;
END;
$$;
------
DROP PROCEDURE get_books_by_genre;
CREATE OR REPLACE PROCEDURE get_books_by_genre(
    IN genre_name VARCHAR,
    OUT book_count INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    CREATE TEMPORARY TABLE temp_books AS
    SELECT b.title, b.author
    FROM books b
    JOIN genres g ON b.genre_id = g.id
    WHERE g.name = genre_name;

    -- cate carti am in temp_Books
    SELECT COUNT(*) INTO book_count FROM temp_books;

    -- Explicitly commit the transaction to make the temporary table visible
    COMMIT;
END;
$$;
--------
--drop function count_books_by_author;

CREATE OR REPLACE PROCEDURE count_books_by_author(
    IN author_name VARCHAR,
    OUT book_count INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT COUNT(*) INTO book_count FROM books WHERE author = author_name;
    COMMIT;
END;
$$;

select *from books;

----pt trigger:
--ALTER TABLE books ADD COLUMN timestamp TIMESTAMP;

DROP TRIGGER IF EXISTS book_insert_trigger ON books;
--ALTER TABLE books DROP COLUMN timestamp;


ALTER TABLE books ADD COLUMN created_at TIMESTAMP;

CREATE OR REPLACE FUNCTION update_created_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.created_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER book_insert_trigger
BEFORE INSERT ON books
FOR EACH ROW
EXECUTE FUNCTION update_created_at();


---pt imprumuturi

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE borrowings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    due_date DATE NOT NULL,
    return_date DATE
);



select *from users;
