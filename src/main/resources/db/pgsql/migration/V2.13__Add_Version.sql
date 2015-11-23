ALTER TABLE logs ADD COLUMN version VARCHAR(20) NOT NULL DEFAULT '1';

UPDATE logs AS l1 
SET version = (SELECT count(entry_id) + 1 FROM logs AS l2 WHERE l2.entry_id = l1.entry_id AND l2.id < l1.id) 
WHERE l1.entry_id IN (SELECT entry_id FROM logs GROUP BY entry_id HAVING count(entry_id) > 1);

