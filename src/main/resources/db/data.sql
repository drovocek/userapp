DELETE
FROM USERS;

ALTER TABLE USERS
    ALTER COLUMN ID RESTART WITH 1;

INSERT INTO USERS (PHONE_NUMBER, EMAIL, FIRST_NAME, LAST_NAME)

VALUES ('+1 (111) 111-11-11', 'vasily1@gmail.com', 'Vasily1', 'Ivanov1'),
       ('+2 (222) 222-22-22', 'ivan1@gmail.com', 'Ivan1', 'Vasiliev1'),
       ('+3 (333) 333-33-33', 'vasily2@gmail.com', 'Vasily2', 'Ivanov2'),
       ('+4 (444) 444-44-44', 'ivan2@gmail.com', 'Ivan2', 'Vasiliev2'),
       ('+5 (555) 555-55-55', 'vasily3@gmail.com', 'Vasily3', 'Ivanov3'),
       ('+6 (666) 666-66-66', 'ivan3@gmail.com', 'Ivan3', 'Vasiliev3'),
       ('+7 (777) 777-77-77', 'vasily4@gmail.com', 'Vasily4', 'Ivanov4'),
       ('+8 (888) 888-88-88', 'ivan4@gmail.com', 'Ivan4', 'Vasiliev4'),
       ('+9 (999) 999-99-99', 'vasily5@gmail.com', 'Vasily5', 'Ivanov5'),
       ('+0 (000) 000-00-00', 'ivan5@gmail.com', 'Ivan5', 'Vasiliev5'),
       ('+1 (111) 111-11-11', 'vasily11@gmail.com', 'Vasily11', 'Ivanov11'),
       ('+2 (222) 222-22-22', 'ivan11@gmail.com', 'Ivan11', 'Vasiliev11'),
       ('+3 (333) 333-33-33', 'vasily22@gmail.com', 'Vasily22', 'Ivanov22'),
       ('+4 (444) 444-44-44', 'ivan22@gmail.com', 'Ivan22', 'Vasiliev22'),
       ('+5 (555) 555-55-55', 'vasily33@gmail.com', 'Vasily33', 'Ivanov33'),
       ('+6 (666) 666-66-66', 'ivan33@gmail.com', 'Ivan33', 'Vasiliev33'),
       ('+7 (777) 777-77-77', 'vasily44@gmail.com', 'Vasily44', 'Ivanov44'),
       ('+8 (888) 888-88-88', 'ivan44@gmail.com', 'Ivan44', 'Vasiliev44'),
       ('+9 (999) 999-99-99', 'vasily55@gmail.com', 'Vasily55', 'Ivanov55'),
       ('+0 (000) 000-00-00', 'ivan55@gmail.com', 'Ivan55', 'Vasiliev55'),
       ('+1 (111) 111-11-11', 'vasily111@gmail.com', 'Vasily111', 'Ivanov111'),
       ('+2 (222) 222-22-22', 'ivan111@gmail.com', 'Ivan111', 'Vasiliev111'),
       ('+3 (333) 333-33-33', 'vasily222@gmail.com', 'Vasily222', 'Ivanov222'),
       ('+4 (444) 444-44-44', 'ivan222@gmail.com', 'Ivan222', 'Vasiliev222'),
       ('+5 (555) 555-55-55', 'vasily333@gmail.com', 'Vasily333', 'Ivanov333'),
       ('+6 (666) 666-66-66', 'ivan333@gmail.com', 'Ivan333', 'Vasiliev333'),
       ('+7 (777) 777-77-77', 'vasily444@gmail.com', 'Vasily444', 'Ivanov444'),
       ('+8 (888) 888-88-88', 'ivan444@gmail.com', 'Ivan444', 'Vasiliev444'),
       ('+9 (999) 999-99-99', 'vasily555@gmail.com', 'Vasily555', 'Ivanov555'),
       ('+0 (000) 000-00-00', 'ivan555@gmail.com', 'Ivan555', 'Vasiliev555');