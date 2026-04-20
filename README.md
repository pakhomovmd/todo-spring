# Todo Spring - Таск-трекер

Веб-приложение для управления задачами с аутентификацией пользователей, построенное на Spring Boot и Angular.

## Описание

Todo Spring - это полнофункциональный таск-трекер, который позволяет пользователям создавать, редактировать и отслеживать свои задачи. Приложение использует JWT-аутентификацию для безопасного доступа и предоставляет REST API для взаимодействия с фронтендом.

## Функциональные возможности

- **Аутентификация и авторизация**
  - Регистрация и вход пользователей
  - JWT токены (Access Token и Refresh Token)
  - Ролевая модель доступа

- **Управление задачами**
  - Создание новых задач
  - Редактирование существующих задач
  - Удаление задач
  - Просмотр списка задач с фильтрацией по датам
  - Отслеживание статуса задач (OPEN, IN_PROGRESS, DONE, CLOSED)
  - Подсчет активных задач

- **Персонализация**
  - Каждый пользователь видит только свои задачи
  - Отслеживание автора и времени создания задачи

## Технологии

### Backend
- **Java 17**
- **Spring Boot 4.0.3**
- **Spring Security** - JWT аутентификация
- **Spring Data JPA** - работа с базой данных
- **PostgreSQL** - основная база данных
- **Maven** - управление зависимостями

### Frontend
- **Angular 19.2.0**
- **TypeScript 5.7**
- **RxJS 7.8** - реактивное программирование
- **Angular Router** - маршрутизация

## Требования

Перед запуском проекта убедитесь, что у вас установлены:

- **Java 17** или выше
- **Node.js 18+** и npm
- **PostgreSQL 12+**
- **Maven 3.6+**

## Установка и запуск

### 1. Настройка базы данных

Создайте базу данных PostgreSQL:

```sql
CREATE DATABASE taskdb;
```

Настройте параметры подключения в файле `todo/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=postgres
spring.datasource.password=admin
```

### 2. Запуск Backend

Перейдите в папку `todo` и выполните:

```bash
cd todo
mvnw clean install
mvnw spring-boot:run
```

Backend будет доступен по адресу: `http://localhost:8080`

### 3. Запуск Frontend

Перейдите в папку `todo-frontend` и выполните:

```bash
cd todo-frontend
npm install
npm start
```

Frontend будет доступен по адресу: `http://localhost:4200`

## API Endpoints

### Аутентификация

- `POST /auth/login` - Вход в систему
- `POST /auth/refresh` - Обновление access token
- `GET /auth/me` - Получение информации о текущем пользователе

### Задачи

- `GET /tasks?userId={id}&from={date}&to={date}` - Получить список задач
- `GET /tasks/{id}` - Получить задачу по ID
- `POST /tasks` - Создать новую задачу
- `PUT /tasks/{id}` - Обновить задачу
- `DELETE /tasks/{id}` - Удалить задачу
- `GET /tasks/active/count?userId={id}` - Количество активных задач

## Структура проекта

```
todo-spring-main/
├── todo/                          # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/ru/ssau/todo/
│   │   │   │   ├── controller/    # REST контроллеры
│   │   │   │   ├── service/       # Бизнес-логика
│   │   │   │   ├── repository/    # Репозитории JPA
│   │   │   │   ├── entity/        # JPA сущности
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── config/        # Конфигурация Security
│   │   │   │   └── filter/        # JWT фильтр
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── todo-frontend/                 # Frontend (Angular)
    ├── src/
    │   ├── app/
    │   │   ├── components/        # Компоненты Angular
    │   │   ├── services/          # Сервисы для API
    │   │   ├── models/            # TypeScript модели
    │   │   ├── interceptors/      # HTTP interceptors
    │   │   └── app.routes.ts      # Маршруты
    │   └── proxy.conf.json        # Прокси для dev-сервера
    ├── angular.json
    └── package.json
```

## Модель данных

### Task (Задача)
- `id` - Уникальный идентификатор
- `title` - Название задачи
- `status` - Статус (OPEN, IN_PROGRESS, DONE, CLOSED)
- `createdBy` - Автор задачи (связь с User)
- `createdAt` - Дата и время создания

### User (Пользователь)
- `id` - Уникальный идентификатор
- `username` - Имя пользователя
- `password` - Хешированный пароль
- `roles` - Роли пользователя

## Разработка

### Backend

Для разработки backend используйте:

```bash
mvnw spring-boot:run
```

Логи SQL запросов включены в `application.properties` для отладки.

### Frontend

Для разработки frontend с hot-reload:

```bash
npm start
```

Angular использует прокси-конфигурацию (`proxy.conf.json`) для перенаправления API запросов на backend.

## Лицензия

Этот проект создан в образовательных целях.

## Автор

[pakhomovmd](https://github.com/pakhomovmd)
