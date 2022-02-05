CREATE TABLE CITIES
(
    ID                SERIAL  NOT NULL PRIMARY KEY,
    ENGLISH_NAME      TEXT    NOT NULL,
    MACEDONIAN_NAME   TEXT    NOT NULL,
    ALBANIAN_NAME     TEXT    NOT NULL
);


INSERT INTO CITIES(ENGLISH_NAME, MACEDONIAN_NAME, ALBANIAN_NAME) VALUES ('SKOPJE', 'СКОПЈЕ', ''),
                        ('BITOLA', 'БИТОЛА',''),
                        ('KUMANOVO', 'КУМАНОВО',''),
                        ('PRILEP', 'ПРИЛЕП',''),
                        ('TETOVO', 'ТЕТОВО',''),
                        ('VELES', 'ВЕЛЕС',''),
                        ('SHTIP', 'ШТИП',''),
                        ('OHRID', 'ОХРИД',''),
                        ('GOSTIVAR', 'ГОСТИВАР',''),
                        ('STRUMICA', 'СТРУМИЦА',''),
                        ('KAVADARCI', 'КАВАДАРЦИ',''),
                        ('KOCHANI', 'КОЧАНИ',''),
                        ('KICHEVO', 'КИЧЕВО',''),
                        ('STRUGA', 'СТРУГА',''),
                        ('RADOVISH', 'РАДОВИШ',''),
                        ('GEVGELIJA', 'ГЕВГЕЛИЈА',''),
                        ('DEBAR', 'ДЕБАР',''),
                        ('KRIVA PALANKA', 'КРИВА ПАЛАНКА',''),
                        ('SVETI NIKOLE', 'СВЕТИ НИКОЛЕ',''),
                        ('NEGOTINO', 'НЕГОТИНО',''),
                        ('DELCHEVO', 'ДЕЛЧЕВО',''),
                        ('VINICA', 'ВИНИЦА',''),
                        ('RESEN', 'РЕСЕН',''),
                        ('PROBISHTIP', 'ПРОБИШТИП',''),
                        ('BEROVO', 'БЕРОВО',''),
                        ('KRATOVO', 'КРАТОВО',''),
                        ('BOGDANCI', 'БОГДАНЦИ',''),
                        ('KRUSHEVO', 'КРУШЕВО',''),
                        ('MAKEDONSKA KAMENICA', 'МАКЕДОНСКА КАМЕНИЦА',''),
                        ('VALANDOVO', 'ВАЛАНДОВО',''),
                        ('MAKEDONSKI BROD', 'МАКЕДОНСКИ БРОД',''),
                        ('DEMIR KAPIJA', 'ДЕМИР КАПИЈА',''),
                        ('PEHCHEVO', 'ПЕХЧЕВО',''),
                        ('DEMIR HISAR', 'ДЕМИР ХИСАР',''),
                        ('VEVCHANI', 'ВЕВЧАНИ',''),
                        ('MAVROVO', 'МАВРОВО','');


ALTER TABLE TRIPS RENAME COLUMN START TO START_CITY_ID;
ALTER TABLE TRIPS RENAME COLUMN "END" TO END_CITY_ID;

ALTER TABLE TRIPS ADD CONSTRAINT START_CITY_ID_FK FOREIGN KEY(START_CITY_ID) REFERENCES CITIES(ID);
ALTER TABLE TRIPS ADD CONSTRAINT END_CITY_ID_FK FOREIGN KEY(END_CITY_ID) REFERENCES CITIES(ID);
