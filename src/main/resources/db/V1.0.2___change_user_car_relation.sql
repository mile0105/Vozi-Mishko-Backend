alter table users drop constraint car_id_constraint;
alter table users drop car_id;

alter table cars add column user_id serial not null;
alter table cars add constraint user_id_constraint foreign key (user_id) references users(id);
