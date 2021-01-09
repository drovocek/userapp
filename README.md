[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9a72b9652c55429f87a3d0e22ff27474)](https://app.codacy.com/gh/drovocek/userapp?utm_source=github.com&utm_medium=referral&utm_content=drovocek/userapp&utm_campaign=Badge_Grade)
[![Build Status](https://www.travis-ci.com/drovocek/userapp.svg?branch=master)](https://www.travis-ci.com/drovocek/userapp)
# Test task

## Completion date 
10 January 2021 (in progress)

## Theme 
Test task for getting a job

## Technology stack
- Java 8
- Spring Boot
- Spring WebSocket
- H2
- Slf4j
- JUnit4
- JQuery, BootStrap

## Documentation
html

pdf

## Task
Сделать мини-веб приложение из одной таблицы с 4-мя полями: Номер, Емейл, Имя, Фамилия.

Фронт на любом фреймворке по выбору или чистом html.

1) Стандартные CRUD операции в интерфейсе пользователя
2) Поиск по одному или нескольким полям.
3) Поддержка одновременной работы нескольких пользователей в приложении
(если два человека одновременно работают над таблицей и один из них внес изменения (и сохранил их), то второй сразу это видит, без перезагрузки страницы)
4) Восстановление сеанса редактирования при ошибках Сети
(по возможности)
5) Кол-во записей в таблице потребует средство прокрутки и перехода по страницам.
6) По возможности (но не обязательно) оптимизировать под большие объемы данных
7) Сервер и клиентская часть должны представлять независимые под-проекты.

Код должен быть максимально похож на законченное приложение, готов к развертыванию в облаке, покрыт тестами.

Делать по-возможности на Spring Boot.
