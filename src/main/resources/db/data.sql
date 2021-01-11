DELETE
FROM USERS;

ALTER TABLE USERS
    ALTER COLUMN ID RESTART WITH 1;

INSERT INTO USERS (PHONE_NUMBER, EMAIL, FIRST_NAME, LAST_NAME)

VALUES ('+7 (111) 111-1111', 'vasily1@gmail.com', 'Vasily1', 'Ivanov1'),
       ('+7 (222) 222-2222', 'ivan1@gmail.com', 'Ivan1', 'Vasiliev1'),
       ('+7 (333) 333-3333', 'vasily2@gmail.com', 'Vasily2', 'Ivanov2'),
       ('+7 (444) 444-4444', 'ivan2@gmail.com', 'Ivan2', 'Vasiliev2'),
       ('+7 (555) 555-5555', 'vasily3@gmail.com', 'Vasily3', 'Ivanov3'),
       ('+7 (666) 666-6666', 'ivan3@gmail.com', 'Ivan3', 'Vasiliev3'),
       ('+7 (777) 777-7777', 'vasily4@gmail.com', 'Vasily4', 'Ivanov4'),
       ('+7 (888) 888-8888', 'ivan4@gmail.com', 'Ivan4', 'Vasiliev4'),
       ('+7 (999) 999-9999', 'vasily5@gmail.com', 'Vasily5', 'Ivanov5'),
       ('+7 (000) 000-0000', 'ivan5@gmail.com', 'Ivan5', 'Vasiliev5'),
       ('+7 (111) 111-1111', 'vasily11@gmail.com', 'Vasily11', 'Ivanov11'),
       ('+7 (222) 222-2222', 'ivan11@gmail.com', 'Ivan11', 'Vasiliev11'),
       ('+7 (333) 333-3333', 'vasily22@gmail.com', 'Vasily22', 'Ivanov22'),
       ('+7 (444) 444-4444', 'ivan22@gmail.com', 'Ivan22', 'Vasiliev22'),
       ('+7 (555) 555-5555', 'vasily33@gmail.com', 'Vasily33', 'Ivanov33'),
       ('+7 (666) 666-6666', 'ivan33@gmail.com', 'Ivan33', 'Vasiliev33'),
       ('+7 (777) 777-7777', 'vasily44@gmail.com', 'Vasily44', 'Ivanov44'),
       ('+7 (888) 888-8888', 'ivan44@gmail.com', 'Ivan44', 'Vasiliev44'),
       ('+7 (999) 999-9999', 'vasily55@gmail.com', 'Vasily55', 'Ivanov55'),
       ('+7 (000) 000-0000', 'ivan55@gmail.com', 'Ivan55', 'Vasiliev55'),
       ('+7 (111) 111-1111', 'vasily111@gmail.com', 'Vasily111', 'Ivanov111'),
       ('+7 (222) 222-2222', 'ivan111@gmail.com', 'Ivan111', 'Vasiliev111'),
       ('+7 (333) 333-3333', 'vasily222@gmail.com', 'Vasily222', 'Ivanov222'),
       ('+7 (444) 444-4444', 'ivan222@gmail.com', 'Ivan222', 'Vasiliev222'),
       ('+7 (555) 555-5555', 'vasily333@gmail.com', 'Vasily333', 'Ivanov333'),
       ('+7 (666) 666-6666', 'ivan333@gmail.com', 'Ivan333', 'Vasiliev333'),
       ('+7 (777) 777-7777', 'vasily444@gmail.com', 'Vasily444', 'Ivanov444'),
       ('+7 (888) 888-8888', 'ivan444@gmail.com', 'Ivan444', 'Vasiliev444'),
       ('+7 (999) 999-9999', 'vasily555@gmail.com', 'Vasily555', 'Ivanov555'),
       ('+7 (000) 000-0000', 'ivan555@gmail.com', 'Ivan555', 'Vasiliev555');
