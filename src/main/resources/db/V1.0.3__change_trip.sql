alter table trips add time_of_departure timestamp not null default '1900-01-01 00:00:00';
alter table trips add car_id integer not null references cars(id);
