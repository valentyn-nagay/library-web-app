# Library Web App (Java + Spring Boot + PostgreSQL + Docker + Auth)

## Быстрый старт

1. Установи Docker и Docker Compose.
2. В корне проекта выполни:

```bash
docker-compose up --build
```

3. После успешного запуска:
   - Приложение (редирект на каталог): http://localhost:8080
   - Каталог книг: http://localhost:8080/books
   - Выдача книг: http://localhost:8080/loans
   - Вход: http://localhost:8080/login
   - Регистрация: http://localhost:8080/register

Создаётся база PostgreSQL `library` с пользователем `library` / `library`.
Начальные пользователи и книги загружаются из `src/main/resources/data.sql`.

## Регистрация и авторизация

- Страница логина: http://localhost:8080/login
- Страница регистрации: http://localhost:8080/register

После регистрации создаётся запись в таблице `library_user` с ролью `USER`.
Доступ ко всем страницам (`/books`, `/loans` и т.д.) только для авторизованных пользователей.
