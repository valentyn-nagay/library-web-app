# Library Web App (Java + Spring Boot + PostgreSQL + Docker + Auth)

Учебное веб-приложение «Библиотека», предназначенное для демонстрации типовой функиональности:
- ведение каталога книг;
- оформление выдачи книг читателям;
- регистрация и авторизация пользователей.

## Технологии

- Java 17
- Spring Boot 3 (Web, Thymeleaf, Data JPA, Security)
- PostgreSQL
- Docker + Docker Compose
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

---

## Быстрый старт

### Вариант 1 — запуск через Docker Compose

1. Установите Docker и Docker Compose.
2. В корне проекта выполните:

   ```bash
   docker-compose up --build
   ```

3. После успешного старта приложения будут доступны следующие URL:

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

### Вариант 2 — запуск локально с использованием Maven Wrapper

Maven Wrapper позволяет запускать проект без предварительной установки Maven в системе:
все необходимые артефакты Maven будут скачаны автоматически.

1. Убедитесь, что установлен JDK 17+
2. В корне проекта (где лежит `pom.xml`) выполните:

   **Linux / macOS:**

   ```bash
   ./mvnw clean package
   ```

   **Windows (cmd / PowerShell):**

   ```bash
   mvnw.cmd clean package
   ```

3. После успешной сборки запустите приложение:

   ```bash
   java -jar target/library-0.0.1-SNAPSHOT.jar
   ```

4. Приложение будет доступно по адресу:

   ```text
   http://localhost:8080
   ```

---

## Подключение к БД

Параметры подключения указаны в `docker-compose.yml`:

- **DB host:** `localhost` (при подключении с хоста), `db` (при подключении из контейнера приложения)
- **DB name:** `library`
- **DB user:** `library`
- **DB password:** `library`
- Порт PostgreSQL, проброшенный наружу: `5432`

Пример строки подключения (например, для DBeaver):

```text
jdbc:postgresql://localhost:5432/library
```

---

## Инициализация данных

При старте Spring Boot автоматически выполняет скрипт `src/main/resources/data.sql`.

Скрипт создаёт:

1. **Несколько тестовых читателей** (без логина/пароля, используются как сущности для выдачи книг):

   ```sql
   INSERT INTO library_user (full_name, email)
   VALUES ('Иван Петров', 'ivan@example.com'),
          ('Мария Смирнова', 'maria@example.com')
   ON CONFLICT DO NOTHING;
   ```

2. **Учетную запись администратора** с заранее заданными логином и парпаролем:

   ```text
   login:  admin
   password: admin
   role: ADMIN
   ```

   В `data.sql` это оформлено следующим образом (пароль сохранён в виде BCrypt-хэша):

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

3. **Стартовый набор книг** (ID генерируются автоматически):

   ```sql
   INSERT INTO book (title, author, publish_year, isbn, total_copies, available_copies, has_ebook, ebook_url)
   VALUES ('Приключения Тома Сойера', 'Марк Твен', 1876, '978-5-699-12014-1', 5, 5, TRUE, 'https://example.com/tom-sawyer.pdf'),
          ('Мастер и Маргарита', 'Михаил Булгаков', 1967, '978-5-389-07473-9', 3, 3, FALSE, NULL)
   ON CONFLICT DO NOTHING;
   ```

Если скрипт `data.sql` был изменён и требуется переинициализировать данные, удобнее всего полностью пересоздать контейнеры и тома:

```bash
docker-compose down -v
docker-compose up --build
```

---

## Регистрация и авторизация

### Вариант 1 — встроенный администратор

Сразу после запуска доступна учётная запись администратора:

- Страница входа: `http://localhost:8080/login`
- Логин: `admin`
- Пароль: `admin`

После успешной аутентификации происходит перенаправление на `/books`.

### Вариант 2 — регистрация нового пользователя

1. Откройте страницу регистрации: `http://localhost:8080/register`.
2. Заполните форму регистрации (ФИО, e-mail, имя пользователя и пароль).
3. Отправьте форму.
4. Выполните вход, используя только что указанное имя пользователя и пароль.

Все пользователи, зарегистрированные через форму, получают роль `USER`
(в коде: `user.setRole("USER")`).

---

## Ограничения доступа

Правила доступа задаются в классе `SecurityConfig` (Spring Security):

- Доступ **без авторизации** разрешён для:
    - `/login`
    - `/register`
    - `/error`
- Все остальные эндпоинты (например, `/books`, `/loans`) доступны **только авторизованным** пользователям.

Формы входа/выхода:

- Вход: `/login` (стандартная форма Spring Security).
- Выход: POST-запрос на `/logout`  
  (в шаблонах используется форма вида `<form action="/logout" method="post">`).

---

## Структура проекта

### Java-код

Корень пакета: `src/main/java/io/github/valentyn/nagay`

- `LibraryApplication` — точка входа в приложение.
- `model` — сущности базы данных:
    - `Book`
    - `LibraryUser`
    - `Loan`
- `repository` — JPA-репозитории:
    - `BookRepository`
    - `LibraryUserRepository`
    - `LoanRepository`
- `service`:
    - `BookService` — операции с каталогом книг;
    - `LoanService` — логика выдачи и возврата книг;
    - `CustomUserDetailsService` — интеграция пользователей с Spring Security.
- `config`:
    - `SecurityConfig` — настройки безопасности, правила доступа, логин/логаут, `PasswordEncoder`.
- `controller`:
    - `HomeController` — начальная точка (редирект с `/` на каталог/панель);
    - `BookController` — управление каталогом, добавление и просмотр книг;
    - `LoanController` — оформление выдачи и возврата книг;
    - `AuthController` — регистрация и авторизация пользователей.
- `web`:
    - `RegistrationForm` — DTO для формы регистрации.

### Ресурсы

Каталог: `src/main/resources`

- `application.yml` — конфигурация БД, JPA и других компонентов.
- `data.sql` — стартовые данные для БД.
- `templates` — Thymeleaf-шаблоны представлений:
    - `books/*.html` — страницы работы с каталогом книг;
    - `loans/*.html` — страницы работы с выдачей книг;
    - `auth/*.html` — страницы входа и регистрации.

---

## Полезные команды

### Сборка и запуск без Docker (через Maven Wrapper)

**Linux / macOS:**

```bash
./mvnw clean package
./mvnw test
```

**Windows:**

```bash
mvnw.cmd clean package
mvnw.cmd test
```

Запуск приложения (после сборки):

```bash
java -jar target/library-0.0.1-SNAPSHOT.jar
```

> Для локального запуска без Docker потребуется запущенный PostgreSQL на `localhost:5432`
> с параметрами доступа, соответствующими `application.yml`.

### Управление контейнерами Docker

Остановка сервисов Docker:

```bash
docker-compose down
```

Полное удаление контейнеров и томов (включая данные БД):

```bash
docker-compose down -v
```
