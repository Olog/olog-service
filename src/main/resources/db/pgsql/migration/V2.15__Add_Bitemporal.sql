DROP TABLE IF EXISTS bitemporal_log;
CREATE TABLE  bitemporal_log (
  id SERIAL NOT NULL,
  log_id int DEFAULT NULL,
  recordinterval TIMESTAMP DEFAULT NULL,
  recordinterval_0 TIMESTAMP DEFAULT NULL,
  validityinterval TIMESTAMP DEFAULT NULL,
  validityinterval_0 TIMESTAMP DEFAULT NULL,
  entry_id int DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (entry_id) REFERENCES entries (id),
  FOREIGN KEY (log_id) REFERENCES logs (id)
);

CREATE INDEX bitemporal_log_id ON bitemporal_log(log_id);
CREATE INDEX bitemporal_entry_id ON bitemporal_log(entry_id);

ALTER TABLE entries ADD COLUMN IF NOT EXISTS state VARCHAR(20) NOT NULL DEFAULT 'Active';

INSERT INTO bitemporal_log (
	log_id, 
	recordinterval_0, 
	recordinterval, 
	validityinterval_0, 
	validityinterval, 
	entry_id)

  (SELECT id, 
         modified, 
	 CASE
		WHEN (SELECT modified FROM logs e WHERE e.id > l.id and e.entry_id=l.entry_id ORDER BY id LIMIT 1) is null 
			THEN '3197-09-13 18:00:00'
	 ELSE
		(SELECT modified FROM logs e WHERE e.id > l.id and e.entry_id=l.entry_id ORDER BY id LIMIT 1)
	 END as recordinterval, 
	 modified, 
	 '3197-09-13 18:00:00',
	 entry_id 
	from logs l);

CREATE INDEX record_0 ON bitemporal_log(recordinterval_0);
CREATE INDEX record ON bitemporal_log(recordinterval);
CREATE INDEX valid_0 ON bitemporal_log(validityinterval_0);
CREATE INDEX valid ON bitemporal_log(validityinterval);

CREATE INDEX bitemporal_log_ids ON bitemporal_log(log_id, entry_id);

UPDATE bitemporal_log SET recordinterval='3197-09-13 18:00:00' where recordinterval_0 > recordinterval;

ALTER TABLE logs_attributes ALTER COLUMN grouping_num TYPE int;
ALTER TABLE logs_attributes ALTER COLUMN grouping_num SET DEFAULT NULL;
