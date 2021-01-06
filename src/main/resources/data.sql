DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;

INSERT INTO USERS (PHONE_NUMBER, EMAIL, FIRST_NAME, LAST_NAME)

VALUES ('+1 (111) 111-11-11', 'vasily@gmail.com', 'Vasily', 'Ivanov'),
       ('+2 (222) 222-22-22', 'ivan@gmail.com', 'Ivan', 'Vasiliev');