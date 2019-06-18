CREATE INDEX IF NOT EXISTS att_value ON logs_attributes (value);
ALTER TABLE logs_attributes DROP CONSTRAINT IF EXISTS logs_attributes_pkey;
ALTER TABLE logs_attributes DROP CONSTRAINT IF EXISTS logs_attributes_log_id_fk;
ALTER TABLE logs_attributes DROP CONSTRAINT IF EXISTS logs_attributes_attribute_id_fk;
ALTER TABLE logs_attributes ADD PRIMARY KEY (id, attribute_id);