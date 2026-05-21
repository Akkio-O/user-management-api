# User Management API
Проект сделан как backend pet-project REST API для управления пользователями и адресами, которые используются в реальной разработке:
- внешняя конфигурация через environment variables;
- миграции БД через Flyway;
- хеширование паролей через BCrypt;
- DTO-валидация;
- глобальная обработка ошибок;
- тестовый профиль с H2;
- контейнеризация через Docker и Docker Compose.

## Стек
- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- BCrypt (`spring-security-crypto`)
- Bean Validation
- Lombok
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- H2 (tests)
- Docker
- Docker Compose
- Maven Wrapper

## Возможности
- создание пользователя;
- получение списка пользователей;
- получение пользователя по UUID;
- обновление пользователя;
- удаление пользователя;
- DTO validation;
- проверка уникальности `login`;
- хранение пароля в виде BCrypt hash;
- миграции БД через Flyway;
- отдельный test profile на H2;
- Swagger/OpenAPI документация;
- запуск приложения и PostgreSQL через Docker Compose.

# Конфигурация
Креды БД хранятся во внешних переменных окружения:
Для локальной разработки можно создать файл `.env` в корне проекта на основе `.env.example`.
## Подготовка базы данных
### Локальный запуск без Docker:
Нужна PostgreSQL-база, например:
```sql
CREATE DATABASE test_db;
```
Схема таблиц создаётся автоматически при старте приложения через Flyway-миграцию:
```text
src/main/resources/db/migration/V1__create_users_and_address_tables.sql
```
В проекте используется режим:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```
Flyway создаёт и изменяет схему БД, а Hibernate проверяет соответствие entity и таблиц
## Запуск приложения
Из корня проекта: Linux / macOS
```bash
./mvnw spring-boot:run
```
Для Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```
Приложение запускается на порту:
```text
http://localhost:8081
```
Swagger UI доступен по адресу:
```text
http://localhost:8081/swagger-ui/index.html
```
## Запуск через Docker Compose
Проект поддерживает запуск Spring Boot и PostgreSQL одной командой через Docker Compose.
### Что используется
- отдельный контейнер PostgreSQL;
- отдельный контейнер Spring Boot;
- healthcheck для ожидания готовности БД;
- volume для хранения данных PostgreSQL;
- env variables через .env.
## Запуск тестов
```bash
./mvnw test
```
Для Windows PowerShell:
```powershell
.\mvnw.cmd test
```
Тесты используют профиль `test` и in-memory базу H2:
```text
src/test/resources/application-test.properties
```
Тесты используют H2 и не требуют твою локальную PostgreSQL и креды из `.env`
## API
Основной путь:
```text
/api/v1/users
```
### Создать пользователя
```http
POST /api/v1/users
Content-Type: application/json
```
Пример запроса:
```json
{
  "login": "evgeniy",
  "password": "password123",
  "firstName": "Евгений",
  "lastName": "Нестеренко",
  "age": 26,
  "address": {
    "city": "Воронеж",
    "street": "Ленина",
    "building": "10"
  }
}
```
Ответ:
```http
201 Created
```
Пароль не возвращается в ответе и сохраняется в БД только в виде BCrypt hash
### Получить всех пользователей
```http
GET /api/v1/users
```
Ответ:
```http
200 OK
```
### Получить пользователя по id
```http
GET /api/v1/users/{id}
```
где `id` — UUID пользователя
### Обновить пользователя
```http
PUT /api/v1/users/{id}
Content-Type: application/json
```
Пример запроса:
```json
{
  "login": "evgeniy_updated",
  "password": "newpass123",
  "firstName": "Евгений",
  "lastName": "Нестеренко",
  "age": 27,
  "address": {
    "city": "Воронеж",
    "street": "Плехановская",
    "building": "15"
  }
}
```
Ответ:
```http
200 OK
```
### Удалить пользователя
```http
DELETE /api/v1/users/{id}
```
Ответ:
```http
204 No Content
```
## Валидация и ошибки
Для входящих DTO используется Bean Validation:
- `login` не должен быть пустым
- `password` не должен быть пустым и должен иметь длину от 8 до 20 символов
- `firstName` и `lastName` не должны быть пустыми
- `address` обязателен
- поля `city`, `street`, `building` внутри адреса не должны быть пустыми
Примеры ошибок:
```http
400 Bad Request
```
- при ошибке валидации
```http
404 Not Found
```
- если пользователь не найден
```http
409 Conflict
```
- если пользователь с таким `login` уже существует
## Структура проекта
```text
src/main/java/com/server
├── config        # Конфигурация приложения
├── controller    # REST-контроллеры
├── dto           # Request/response DTO
├── entity        # JPA entity
├── exceptions    # Обработка ошибок
├── mapper        # Маппинг entity <-> DTO
├── repository    # Spring Data JPA repositories
└── service       # Бизнес-логика

src/main/resources
├── application.properties
└── db/migration  # Flyway SQL миграция

src/test
├── java          # Тесты
└── resources     # application-test.properties
```
## Дополнительно
- конфигурация БД вынесена во внешние environment variables;
- .env используется только локально и игнорируется Git;
- Flyway управляет схемой БД;
- Hibernate работает в режиме ddl-auto=validate;
- PostgreSQL контейнеризирован через Docker Compose;
- данные PostgreSQL сохраняются через Docker volume;
- REST API возвращает корректные HTTP-статусы: `201`, `200`, `204`, `400`, `404`, `409`.

## Планируемое масштабирование
- JWT authentication;
- роли и права доступа;
- пагинация;
- фильтрация;
- MockMvc tests;
- Testcontainers;
- CI/CD pipeline.