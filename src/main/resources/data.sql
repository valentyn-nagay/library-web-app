-- Стартовые читатели (без логина/пароля, только для выдачи книг)
INSERT INTO library_user (full_name, email)
VALUES ('Иван Петров', 'ivan@example.com'),
       ('Мария Смирнова', 'maria@example.com')
    ON CONFLICT DO NOTHING;

-- Стартовые книги (БЕЗ явного id — пусть генерируется автоинкрементом)
INSERT INTO book (title, author, publish_year, isbn, total_copies, available_copies, has_ebook, ebook_url)
VALUES ('Приключения Тома Сойера', 'Марк Твен', 1876, '978-5-699-12014-1', 5, 5, TRUE, 'https://example.com/tom-sawyer.pdf'),
       ('Мастер и Маргарита', 'Михаил Булгаков', 1967, '978-5-389-07473-9', 3, 3, FALSE, NULL)
    ON CONFLICT DO NOTHING;

