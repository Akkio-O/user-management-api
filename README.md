# User Management API
Backend pet-project на Spring Boot: REST API для управления пользователями и их адресами.  

Проект собран как упрощённая backend-система с базовыми практиками промышленной разработки:
- внешняя конфигурация через environment variables
- хеширование паролей (BCrypt)
- DTO-валидация
- контейнеризация через Docker и Docker Compose
## Стек
- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- Flyway
- BCrypt (`spring-security-crypto`)
- Bean Validation
- Lombok
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- H2 (test profile)
- Docker + Docker Compose (Spring Boot + PostgreSQL)
- healthcheck зависимостей
- Конфигурация через profiles / env separation
- Maven Wrapper
## Особенности
- CRUD API для управления пользователями и их адресами
- DTO ↔ Entity маппинг через отдельный слой Mapper
- валидация входящих DTO (Bean Validation)
- проверка уникальности `login` на уровне бизнес-логики
- безопасное хранение паролей (BCrypt hash)
- глобальная обработка ошибок (`@ControllerAdvice`)
- пагинация и сортировка через Spring Data Pageable
- транзакционное управление сервисным слоем (`@Transactional`)
- миграции базы данных через Flyway (управление схемой БД)
- тестовый профиль с in-memory базой (H2)
- документирование API через Swagger / OpenAPI
- контейнеризация приложения и базы данных через Docker Compose
## Конфигурации
Конфигурация задаётся через environment variables.
Пример `.env`:
```env
DB_URL=jdbc:postgresql://postgres-db:5432/test_db
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
DB_NAME=test_db
SPRING_PORT=8081
DB_PORT=5432
```
## Запуск БД
Локальный запуск без Docker:
```sql
CREATE DATABASE test_db;
```
Схема таблиц создаётся автоматически через Flyway-миграцию:
```text
src/main/resources/db/migration/V1__create_users_and_address_tables.sql
```
Режимы:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```
Flyway создаёт и изменяет схему БД, а Hibernate проверяет соответствие entity и таблиц
## Запуск приложения
Локально
```bash
./mvnw spring-boot:run
```
Windows:
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
Быстрый запуск:
```bash
docker compose up --build
```
Приложение и база данных запускаются в отдельных контейнерах с помощью Docker Compose, включая healthcheck для ожидания готовности базы данных перед запуском приложения
Компоненты:
- Spring Boot app container
- PostgreSQL container
- healthcheck для БД
- volume для хранения данных
- env конфигурации
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
Тесты используют H2 и не требуют локальную PostgreSQL и креды из `.env`
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
├── ENUM          # Набор предопределенных констант
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
- Hibernate работает в режиме ddl-auto=validate;
- PostgreSQL контейнеризирован через Docker Compose;
- данные PostgreSQL сохраняются через Docker volume;
- REST API возвращает корректные HTTP-статусы: `201`, `200`, `204`, `400`, `404`, `409`.
## Планируемое масштабирование
- JWT authentication;
- роли и права доступа;
- MockMvc tests;
- Testcontainers;
- CI/CD pipeline.
## Архитектура
Многоуровневая архитектура: Контроллер → Сервис → Репозиторий → База данных

DTO изолирует API-контракт от JPA entity, предотвращая утечку модели базы данных в слой представления

Бизнес-логика вынесена в service-слой, контроллеры не содержат бизнес-правил