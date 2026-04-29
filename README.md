# AirBooking

![CI](https://github.com/fr2eof/AirBooking/actions/workflows/ci.yml/badge.svg)

Сервис для управления бронированием авиаперелётов.

## Основной стек
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Kafka](https://img.shields.io/badge/Kafka-3.7-black)
![Liquibase](https://img.shields.io/badge/Liquibase-migrations-lightgrey)
![Testcontainers](https://img.shields.io/badge/Testcontainers-integration%20tests-blueviolet)
![Redis](https://img.shields.io/badge/Redis-cache-red)
![Micrometer](https://img.shields.io/badge/Micrometer-metrics-green)
![Grafana](https://img.shields.io/badge/Grafana-observability-orange)

## Описание

Проект реализует один из микросервисов backend-системы для создания и управления бронированиями авиабилетов. 
Поддерживает асинхронную обработку событий через Kafka и хранение данных в PostgreSQL

Архитектура основана на подходе event-driven и outbox pattern для надёжной доставки событий

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
- Реализовать Payment Service
- Подключить SAGA (хореография)
- DLQ (dead letter events)


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

