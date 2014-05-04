# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table spieler (
  id                        bigint not null,
  vorname                   varchar(255),
  nachname                  varchar(255),
  birthday                  timestamp,
  geschlecht                boolean,
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_spieler primary key (id))
;

create sequence spieler_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists spieler;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists spieler_seq;

