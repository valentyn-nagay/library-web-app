INSERT INTO library_user (id, full_name, email)
VALUES (1, 'Иван Петров', 'ivan@example.com'),
       (2, 'Мария Смирнова', 'maria@example.com')
ON CONFLICT DO NOTHING;

INSERT INTO book (id, title, author, publish_year, isbn, total_copies, available_copies, has_ebook, ebook_url)
VALUES (1, 'Приключения Тома Сойера', 'Марк Твен', 1876, '978-5-699-12014-1', 5, 5, true, 'https://example.com/tom-sawyer.pdf'),
       (2, 'Мастер и Маргарита', 'Михаил Булгаков', 1967, '978-5-389-07473-9', 3, 3, false, null)
ON CONFLICT DO NOTHING;
