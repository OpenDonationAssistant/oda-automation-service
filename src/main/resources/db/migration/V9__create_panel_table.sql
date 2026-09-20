create table automationpanel (
  id varchar(255),
  name varchar(255),
  recipient_id varchar(255),
  cards jsonb not null,
  primary key (id)
);
