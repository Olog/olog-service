-- Postgres DB

set client_min_messages='warning';

--
-- Create schema ologdb4
--
DROP DATABASE IF EXISTS olog;
CREATE DATABASE olog;
\connect olog;

set client_min_messages='warning';

--
-- Definition of table entries
--

DROP TABLE IF EXISTS entries;
CREATE TABLE entries (
  id SERIAL,
  created TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX created_fk ON entries (created);

--
-- Definition of table logbooks
--


DROP TABLE IF EXISTS logbooks;
CREATE TABLE logbooks (
  id SERIAL,
  name varchar(45) NOT NULL UNIQUE,
  is_tag smallint NOT NULL DEFAULT '0',
  owner varchar(45) DEFAULT NULL,
  state VARCHAR(20) NOT NULL DEFAULT 'Active',
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX name_fx ON logbooks (name);

--
-- Definition of table logs
--


DROP TABLE IF EXISTS logs;
CREATE TABLE logs (
  id SERIAL,
  modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  source varchar(80) NOT NULL DEFAULT '',
  owner varchar(32) NOT NULL,
  level VARCHAR(20) NOT NULL DEFAULT 'Info',
  state VARCHAR(20) NOT NULL DEFAULT 'Active',
  description varchar(200000) NOT NULL,
  md5entry varchar(32) NOT NULL DEFAULT '',
  entry_id bigint NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX entry_id_fk ON logs (entry_id);
CREATE INDEX modified_fk ON logs (modified);
ALTER TABLE logs ADD CONSTRAINT entry_id_fk FOREIGN KEY (entry_id) REFERENCES entries (id);


--
-- Definition of table logs_logbooks
--

DROP TABLE IF EXISTS logs_logbooks;
CREATE TABLE logs_logbooks (
  id SERIAL,
  log_id bigint NOT NULL,
  logbook_id bigint NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX log_id_tk ON logs_logbooks (log_id);
CREATE INDEX logbook_id_tk ON logs_logbooks (logbook_id);
ALTER TABLE logs_logbooks ADD CONSTRAINT log_id_fk FOREIGN KEY (log_id) REFERENCES logs (id);
ALTER TABLE logs_logbooks ADD CONSTRAINT logbook_id_fk FOREIGN KEY (logbook_id) REFERENCES logbooks (id);


--
-- Definition of table properties
--

DROP TABLE IF EXISTS properties;
CREATE TABLE  properties (
  id SERIAL,
  name varchar(200) NOT NULL,
  state VARCHAR(20) NOT NULL DEFAULT 'Active',
  PRIMARY KEY (id)
);
CREATE INDEX name_prop_tk ON properties (name);


--
-- Definition of table attributes
--

DROP TABLE IF EXISTS attributes;
CREATE TABLE  attributes (
  id SERIAL,
  property_id bigint NOT NULL,
  name varchar(200) NOT NULL,
  state VARCHAR(20) NOT NULL DEFAULT 'Active',
  PRIMARY KEY (id)
);
CREATE INDEX property_id_tk ON attributes (id);
CREATE INDEX attribute_name_tk ON attributes (name);

ALTER TABLE attributes ADD CONSTRAINT attributes_property_id_fk FOREIGN KEY (property_id) REFERENCES properties (id);

--
-- Definition of table logs_attributes
--

DROP TABLE IF EXISTS logs_attributes;
CREATE TABLE  logs_attributes (
  id SERIAL,
  log_id bigint NOT NULL,
  attribute_id bigint NOT NULL,
  value varchar(200) NOT NULL,
  grouping_num int NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX logs_attributes_log_id_tk ON logs_attributes (log_id);
CREATE INDEX logs_attributes_attribute_id_tk ON logs_attributes (attribute_id);

ALTER TABLE logs_attributes ADD CONSTRAINT logs_attributes_attribute_id_fk FOREIGN KEY (attribute_id) REFERENCES attributes (id);
ALTER TABLE logs_attributes ADD CONSTRAINT logs_attributes_log_id_fk FOREIGN KEY (log_id) REFERENCES logs (id);


--
-- Definition of table subscriptions
--

DROP TABLE IF EXISTS subscriptions;
CREATE TABLE subscriptions (
  id SERIAL,
  tag_id bigint NOT NULL,
  email varchar(250) NOT NULL DEFAULT '',
  webhook varchar(250) DEFAULT NULL,
  level VARCHAR(20) NOT NULL DEFAULT 'Info',
  PRIMARY KEY (id)
);
ALTER TABLE subscriptions ADD CONSTRAINT subscriptions_tag_id_fk FOREIGN KEY (tag_id) REFERENCES logbooks (id);
