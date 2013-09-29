ALTER TABLE `logs_logbooks` 
 DROP COLUMN `state`,
 DROP COLUMN `created`,
 DROP COLUMN `status_id`, 
 DROP INDEX `logs_logbooks_status_id_fk`,
 DROP FOREIGN KEY `logs_logbooks_status_id_fk`;