# Library Web App (Java + Spring Boot + PostgreSQL + Docker)

## Быстрый старт

1. Установи Docker и Docker Compose.
2. В корне проекта выполни:

```bash
docker-compose up --build
```

3. После успешного запуска:
   - Приложение: http://localhost:8080 (редирект на `/books`)
   - Каталог книг: http://localhost:8080/books
   - Выдача книг: http://localhost:8080/loans

Создаётся база PostgreSQL `library` с пользователем `library` / `library`.
Начальные пользователи и книги загружаются из `src/main/resources/data.sql`.
