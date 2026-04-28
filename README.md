# AirBooking

Сервис для управления бронированием авиаперелётов.

## Описание

Проект реализует один из микросервисов backend-системы для создания и управления бронированиями авиабилетов. 
Поддерживает асинхронную обработку событий через Kafka и хранение данных в PostgreSQL

Архитектура основана на подходе event-driven и outbox pattern для надёжной доставки событий

## Основной стек

- Java 17
- Spring Boot 3.5.14
- Spring Data JPA
- Kafka 3.7
- PostgreSQL 17
- Liquibase
- Testcontainers

## Основные возможности

- Создание и управление бронированиями
- Публикация событий в Kafka
- Outbox pattern для гарантированной доставки событий
- Асинхронная обработка сообщений
- Интеграционные тесты с Testcontainers

## Архитектурные особенности

- Outbox pattern для надёжной доставки событий в Kafka
- Асинхронная обработка событий

## Планы развития
- Добавить ретраи из Outbox в Kafka
- Добавить кэширование (Redis)
- Добавить AOP-логи
- Добавить метрики (Prometheus + Micrometer + Grafana)
- Добавить CI/CD (pipeline)

## Запуск проекта

1. Поднять инфраструктуру:
```bash
docker-compose up -d
````

2. Запустить приложение:

```bash
./mvnw spring-boot:run
```

## Конфигурация

Основные настройки задаются в `application.yml`:

* Kafka bootstrap servers
* Database connection
* Liquibase migrations
* Spring profiles

## Тестирование

Запуск тестов:

```bash
./mvnw test
```

Используются интеграционные тесты с Testcontainers (Kafka, PostgreSQL)

