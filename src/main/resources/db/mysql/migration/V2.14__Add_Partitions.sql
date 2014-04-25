CREATE INDEX att_value ON logs_attributes (value);
ALTER TABLE logs_attributes MODIFY id INT NOT NULL;
ALTER TABLE logs_attributes DROP primary key;
ALTER TABLE logs_attributes DROP FOREIGN KEY logs_attributes_log_id_fk;
ALTER TABLE logs_attributes DROP FOREIGN KEY logs_attributes_attribute_id_fk;
ALTER TABLE logs_attributes ADD KEY (id, attribute_id);
ALTER TABLE logs_attributes MODIFY  id INT  AUTO_INCREMENT;
ALTER TABLE logs_attributes partition by hash(attribute_id) partitions 35;