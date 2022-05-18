create table documents (
  id serial not null primary key,
  owner_id serial not null,
  receiver_first_name text not null,
  receiver_phone_number text not null,
  format varchar(10) not null,
  constraint owner_id_constraint foreign key(owner_id) references users(id)
);


alter table trips add column trip_price decimal not null default 0;
alter table trips add column document_price decimal not null default 0;

alter table trips add column maximum_number_of_documents integer not null default 10;

alter table documents add column trip_id serial not null;
alter table documents add constraint trip_id_constraint foreign key(trip_id) references trips(id);
