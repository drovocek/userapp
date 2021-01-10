DELETE
FROM USERS;

ALTER TABLE USERS
    ALTER COLUMN ID RESTART WITH 1;

INSERT INTO USERS (first_name, last_name, phone_number, email)

VALUES ('Vasily', 'Ivanov', '+1 (111) 111-11-11', 'vasily@gmail.com'),
       ('Ivan', 'Vasiliev', '+2 (222) 222-22-22', 'ivan@gmail.com');
