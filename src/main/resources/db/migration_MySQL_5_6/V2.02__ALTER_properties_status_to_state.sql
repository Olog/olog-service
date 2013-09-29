ALTER TABLE `properties` 
 ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `name`;

UPDATE `properties` 
 SET `state`='Active' WHERE `status_id`='1';

UPDATE `properties` 
 SET `state`='Inactive' WHERE `status_id`='2';

ALTER TABLE `properties` 
 DROP COLUMN `status_id`;

ALTER TABLE `properties` 
 ADD UNIQUE INDEX `name`(`name`);

