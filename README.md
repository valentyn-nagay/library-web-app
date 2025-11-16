
# Library Web App (Java + Spring Boot + PostgreSQL + Docker + Auth)

Небольшое учебное веб-приложение «Библиотека»:
каталог книг, выдача книг читателям, регистрация и авторизация пользователей.

## Технологии

- Java 17
- Spring Boot 3 (Web, Thymeleaf, Data JPA, Security)
- PostgreSQL
- Docker + Docker Compose

---

## Быстрый старт

1. Установи Docker и Docker Compose.
2. В корне проекта выполни:

```bash
docker-compose up --build
```

3. После успешного запуска доступны адреса:

- Приложение (редирект на каталог):  
  `http://localhost:8080`
- Каталог книг:  
  `http://localhost:8080/books`
- Выдача книг:  
  `http://localhost:8080/loans`
- Страница входа:  
  `http://localhost:8080/login`
- Страница регистрации:  
  `http://localhost:8080/register`

### Подключение к БД

В `docker-compose.yml`:

- **DB host:** `localhost` (с хоста), `db` (из контейнера приложения)
- **DB name:** `library`
- **DB user:** `library`
- **DB password:** `library`
- Порт PostgreSQL наружу: `5432`

Можно подключаться, например, через DBeaver:  
`jdbc:postgresql://localhost:5432/library`

---

## Инициализация данных

При старте Spring Boot выполнит `src/main/resources/data.sql`.

Там создаются:

1. **Пара тестовых читателей** (без логина/пароля, только как сущности для выдачи книг):

```sql
INSERT INTO library_user (full_name, email)
VALUES ('Иван Петров', 'ivan@example.com'),
       ('Мария Смирнова', 'maria@example.com')
ON CONFLICT DO NOTHING;
```

2. **Дефолтный администратор** с логином и паролем:

```text
login:  admin
password: admin
role: ADMIN
```

В `data.sql` это выглядит так (пароль сохранён как BCrypt-хэш):

```sql
INSERT INTO library_user (full_name, email, username, password, role)
VALUES (
    'Администратор',
    'admin@example.com',
    'admin',
    '$2a$10$Ihk05VSds5rUSgMdsMVi9OKMIx2yUvMz7y9VP3rJmQeizZLrhLMyq',
    'ADMIN'
)
ON CONFLICT DO NOTHING;
```

3. **Стартовые книги** (ID не задаём вручную, они генерируются автоматически):

```sql
INSERT INTO book (title, author, publish_year, isbn, total_copies, available_copies, has_ebook, ebook_url)
VALUES ('Приключения Тома Сойера', 'Марк Твен', 1876, '978-5-699-12014-1', 5, 5, TRUE, 'https://example.com/tom-sawyer.pdf'),
       ('Мастер и Маргарита', 'Михаил Булгаков', 1967, '978-5-389-07473-9', 3, 3, FALSE, NULL)
ON CONFLICT DO NOTHING;
```

> Если меняешь `data.sql`, инициализацию проще всего «обнулить» так:
> ```bash
> docker-compose down -v
> docker-compose up --build
> ```

---

## Регистрация и авторизация

### Вариант 1 — дефолтный админ

Сразу после запуска можно зайти под встроенным админом:

- Страница: `http://localhost:8080/login`
- Логин: `admin`
- Пароль: `admin`

После входа происходит редирект на `/books`.

### Вариант 2 — регистрация нового пользователя

1. Открыть `http://localhost:8080/register`
2. Заполнить форму:
    - Полное имя
    - E-mail (необязательно)
    - Логин
    - Пароль
3. После успешной регистрации тебя перебросит на `/login`.
4. Входишь под только что созданными логином и паролем.

Все зарегистрированные пользователи получают роль `USER` (в коде: `user.setRole("USER")`).

---

## Ограничения доступа

Настройки Spring Security (класс `SecurityConfig`):

- Доступ **без авторизации**:
    - `/login`
    - `/register`
    - `/error`
- Все остальные эндпоинты (`/books`, `/loans` и т.п.) — **только для авторизованных**.

Формы входа/выхода:

- Вход: `/login` (Spring Security form login)
- Выход:
    - POST-запрос на `/logout` (в шаблонах есть форма `<form action="/logout" method="post">`)

---

## Структура проекта

- `src/main/java/com/example/library`
    - `LibraryApplication` — точка входа
    - `model` — сущности БД:
        - `Book`
        - `LibraryUser`
        - `Loan`
    - `repository` — JPA-репозитории:
        - `BookRepository`
        - `LibraryUserRepository`
        - `LoanRepository`
    - `service`:
        - `BookService` — работа с каталогом книг
        - `LoanService` — выдача/возврат книг
        - `CustomUserDetailsService` — интеграция пользователей с Spring Security
    - `config`:
        - `SecurityConfig` — правила доступа, логин/логаут, PasswordEncoder
    - `controller`:
        - `HomeController` — редирект с `/` на `/books`
        - `BookController` — каталог, добавление, детальная страница
        - `LoanController` — выдача/возврат книг
        - `AuthController` — логин/регистрация
    - `web`:
        - `RegistrationForm` — DTO для формы регистрации

- `src/main/resources`
    - `application.yml` — настройки БД и JPA
    - `data.sql` — стартовые данные
    - `templates` — Thymeleaf-шаблоны:
        - `books/*.html`
        - `loans/*.html`
        - `auth/*.html`

---

## Полезные команды

Собрать проект локально (без Docker):

```bash
mvn clean package
```

Запустить локально (при запущенном PostgreSQL на `localhost:5432`):

```bash
java -jar target/library-0.0.1-SNAPSHOT.jar
```

Остановить Docker-сервисы:

```bash
docker-compose down
```

Полностью снести контейнеры + данные БД:

```bash
docker-compose down -v
```
