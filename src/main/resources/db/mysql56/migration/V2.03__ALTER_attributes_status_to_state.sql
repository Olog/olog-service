ALTER TABLE `attributes` 
 ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `name`;

UPDATE `attributes` 
 SET `state`='Active' WHERE `status_id`='1';

UPDATE `attributes` 
 SET `state`='Inactive' WHERE `status_id`='2';

ALTER TABLE `attributes` 
 DROP FOREIGN KEY `attributes_property_id_fk`;

ALTER TABLE `attributes`
 DROP COLUMN `status_id`;


