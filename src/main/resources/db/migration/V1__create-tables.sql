create table automationvariable (
  id varchar(255),
  type varchar(255),
  name varchar(255),
  recipient_id varchar(255),
  value text
);

create table automationrule (
  id varchar(255),
  name varchar(255),
  recipient_id varchar(255),
  triggers jsonb not null,
  actions jsonb not null
);
