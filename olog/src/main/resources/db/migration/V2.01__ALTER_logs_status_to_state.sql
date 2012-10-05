ALTER TABLE `logs` ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `parent_id`;
UPDATE `logs` SET `state`='Active' WHERE `status_id`='1';
UPDATE `logs` SET `state`='Inactive' WHERE `status_id`='2';
ALTER TABLE `logs` MODIFY COLUMN `id` INT(11) NOT NULL,
 DROP COLUMN `status_id`, 
 DROP INDEX `status_id_fk`,
 DROP FOREIGN KEY `status_id_fk`;
