create table if not exists obs_settings (
  id uuid primary key,
  recipient_id varchar(255) not null unique,
  websocket_url varchar(1024) not null,
  password varchar(1024)
);

create table if not exists meld_settings (
  id uuid primary key,
  recipient_id varchar(255) not null unique,
  websocket_url varchar(1024) not null
);
