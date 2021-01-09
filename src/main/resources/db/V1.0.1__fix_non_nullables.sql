alter table users drop constraint "users_first_name_key";
alter table users drop constraint "users_last_name_key";
alter table users alter column car_id drop not null;
