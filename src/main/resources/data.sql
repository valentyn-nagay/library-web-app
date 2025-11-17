-- Стартовые читатели (без логина/пароля, только сущности для выдачи)
INSERT INTO library_user (full_name, email)
VALUES ('Иван Петров', 'ivan@example.com'),
       ('Мария Смирнова', 'maria@example.com')
    ON CONFLICT DO NOTHING;

-- Администратор с логином/паролем admin / admin (пароль в BCrypt)
INSERT INTO library_user (full_name, email, username, password, role)
VALUES (
           'Администратор',
           'admin@example.com',
           'admin',
           '$2a$10$Ihk05VSds5rUSgMdsMVi9OKMIx2yUvMz7y9VP3rJmQeizZLrhLMyq',
           'ADMIN'
       )
    ON CONFLICT DO NOTHING;

--- Open Source Books (локальные PDF)

INSERT INTO book (title, author, publish_year, isbn, total_copies, available_copies, has_ebook, ebook_url)
VALUES
    ('Advanced Linux Programming',
     'Mark Mitchell, Jeffery Oldham, Alex Samuel',
     NULL,
     '978-0735710436',
     3, 3, TRUE,
     '/ebooks/advanced-linux-programming.pdf'),

    ('GNU Diffutils Manual',
     'D. MacKenzie, P. Eggert, R. Stallman',
     NULL,
     '978-0954161750',
     3, 3, TRUE,
     '/ebooks/diffutils.pdf'),

    ('Free Software, Free Society (Hardcover Edition)',
     'Richard M. Stallman',
     2015,
     '9780983159254',
     3, 3, TRUE,
     '/ebooks/fsfs3-hardcover.pdf'),

    ('Git Magic',
     'Ben Lynn',
     NULL,
     NULL,
     3, 3, TRUE,
     '/ebooks/gitmagic.pdf'),

    ('Perl Programer''s Reference Guide',
     'Larry Wall and others',
     NULL,
     '978-1906966027',
     3, 3, TRUE,
     '/ebooks/Perl Programmers Reference Guide.pdf'),

    ('The Guile Reference Manual',
     'The Guile Developers',
     NULL,
     '978-1906966157',
     3, 3, TRUE,
     '/ebooks/The Guile Reference Manual.pdf'),

    ('Think Python, 2nd edition',
     'Allen B. Downey',
     2015,
     '9781491939369',
     3, 3, TRUE,
     '/ebooks/thinkpython2.pdf');

