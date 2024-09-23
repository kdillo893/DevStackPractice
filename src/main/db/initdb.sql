-- initialize the schema and tables with basic stuff..

-- DROP DATABASE crud;
-- CREATE DATABASE crud;

--below only work if supplying this script to psql directly, comment out otherwise.
-- \g
-- \c crud

--select the crud db for operations after this.

-- DROP SCHEMA webapp;
CREATE SCHEMA IF NOT EXISTS webapp;

--User table
CREATE TABLE IF NOT EXISTS  webapp.users (
  uid UUID NOT NULL DEFAULT gen_random_uuid(),
  first_name varchar(160),
  last_name varchar(160),
  email varchar(256),
  pass_hash varchar(256),
  pass_salt varchar(256),
  created timestamp,
  updated timestamp,
  PRIMARY KEY(uid)
);

--Document table
CREATE TABLE IF NOT EXISTS webapp.documents (
  docid bigint NOT NULL GENERATED AS IDENTITY, 
  uid UUID,
  doc_url varchar(256),
  filename varchar(500),
  created timestamp,
  updated timestamp,
  PRIMARY KEY(docid)
  FOREIGN KEY(uid) REFERENCES webapp.user(uid)
);

--TODO: initialize with some basic values

