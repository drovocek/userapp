[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9a72b9652c55429f87a3d0e22ff27474)](https://app.codacy.com/gh/drovocek/userapp?utm_source=github.com&utm_medium=referral&utm_content=drovocek/userapp&utm_campaign=Badge_Grade)
[![Build Status](https://www.travis-ci.com/drovocek/userapp.svg?branch=master)](https://www.travis-ci.com/drovocek/userapp)

# Test task
:link: [**DEMO**](https://websocketuserapp.herokuapp.com/)

## Completion date 
:heavy_check_mark: 10 January 2021

## Technology stack
- Java 8
- Spring Boot
- Spring WebSocket
- PostgreSQL
- Slf4j
- JUnit4
- JQuery, BootStrap

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

## Documentation

### Endpoints

:star: **Stomp:**

  - ``` /websocket ``` - root connection point

:phone: **Send:**

  - ``` /app/users/get ``` - get one
  - ``` /app/users/getAll ``` - get all
  - ``` /app/users/create ``` - create new 
  - ``` /app/users/update/{id} ``` - update 
  - ``` /app/users/delete/{id} ``` - delete 

:mailbox_with_no_mail: **Subscribe:**

- ``` /user/queue/users ``` - receive response by ```get``` and ```getAll``` request 
- ``` /user/queue/errors ``` - receive errors resulting from your actions
- ``` /topic/users ``` - get all updates for ```create```, ```update```, ```delete``` operations

### Transfer objects
:email: **Request:**
```json
{
    "id":"",
    "firstName":"",
    "lastName":"",
    "phoneNumber":"",
    "email":""
}
```

| Param       | Type   | Constraints :warning:                             |
| ---------- | ------ | ---------------------------------- | 
| `id` | String | Blank for create new user | 
| `firstName` | String | Not blank, size must be between 0 and 50                          | 
| `lastName` | String | Not blank, size must be between 0 and 50 | 
| `phoneNumber` | String | Not blank, must be in format: +d{1,3}_(ddd)_ddd-dddd                          | 
| `email` | String | Not blank, email format, not already contained in the table, size must be between 0 and 50 | 

:package: **Response:**
```json
{
  "packageType":"",
  "sessionIdRegex":"",
  "deletedIds":[],
  "users":[{
            "id":0,
            "firstName":"",
            "lastName":"",
            "phoneNumber":"",
            "email":""
          }],
  "apiError":{
              "type":"",
              "typeMessage":"",
              "details":[]
             }
}
```

| Param       | Type   | Description                             |
| ---------- | ------ | ---------------------------------- | 
| `packageType` | String | Type of response content: "GET", "GET_ALL", "UPDATE", "CREATE", "DELETE", "ERROR" | 
| `sessionIdRegex` | String | Part of session id                         | 
| `deletedIds` | Array | Deleted users ids (Number) | 
| `users` | Object | User entity                         | 
| `apiError` | Object | Error entity | 
| `type` | String | Type of error: "DATA_NOT_FOUND", "VALIDATION_ERROR", "APP_ERROR" |
| `typeMessage` | String | Error description |
| `details` | String | Details of error (String) |

### Errors
| Type                 | TypeMessage    | Details     | Description 
| ------------------- | --------------- | -------------------- | --------------------------- |
| DATA_NOT_FOUND     | Data not found   | User with this id is not exist | You sent an id for which there is no entity                              |
| VALIDATION_ERROR   | Validation error | Different options depending on the violated constraints   | Field constraint violated for transfer object| 
| APP_ERROR         | Application error | Some unknown shit    | You did something really bad |
