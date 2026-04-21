alter table alert_data add level_name varchar(512);
alter table alert_data add count integer;

alter table alert_link add event varchar(255);
update alert_link set event='payment';
