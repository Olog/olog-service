ALTER TABLE `logbooks` 
ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `status_id`;


UPDATE `logbooks` SET `state`='Active' WHERE `status_id`='1';

UPDATE `logbooks` SET `state`='Inactive' WHERE `status_id`='2';

ALTER TABLE `logbooks` 
  DROP COLUMN `status_id`,
  DROP INDEX `logbooks_status_id_fk`,
  DROP FOREIGN KEY `logbooks_status_id_fk`;
