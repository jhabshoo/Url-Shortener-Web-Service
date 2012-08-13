# Url schema
# --- !Ups

create table urlTable (
  id    bigserial NOT NULL,
  fullUrl  varchar(128) NOT NULL,
  shortUrl varchar(12) NOT NULL
);

create sequence s_urlTable_id;


# --- !Downs

drop sequence s_urlTable_id;
drop table urlTable;

