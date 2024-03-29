CREATE TABLE RIDE_REQUESTS
(
  ID SERIAL NOT NULL PRIMARY KEY,
  START_CITY_ID SERIAL NOT NULL,
  END_CITY_ID SERIAL NOT NULL,
  TIME_OF_DEPARTURE TIMESTAMP NOT NULL,
  PASSENGER_ID SERIAL NOT NULL,
  TRIP_ID SERIAL,
  IS_CONFIRMED BOOLEAN NOT NULL,

  CONSTRAINT START_CITY_ID_CONSTRAINT FOREIGN KEY(START_CITY_ID) REFERENCES CITIES(ID),
  CONSTRAINT END_CITY_ID_CONSTRAINT FOREIGN KEY(END_CITY_ID) REFERENCES CITIES(ID),
  CONSTRAINT PASSENGER_ID_CONSTRAINT FOREIGN KEY(PASSENGER_ID) REFERENCES USERS(ID),
  CONSTRAINT TRIP_ID_CONSTRAINT FOREIGN KEY(TRIP_ID) REFERENCES TRIPS(ID)
);
