ALTER TABLE `logs`
 ADD COLUMN `level` ENUM('Info','Problem','Request','Suggestion','Urgent') NOT NULL DEFAULT 'Info' AFTER `state`;

UPDATE `logs`
 SET `level`='Info' WHERE `level_id`='1';

UPDATE `logs`
 SET `level`='Problem' WHERE `level_id`='2';

UPDATE `logs`
 SET `level`='Request' WHERE `level_id`='3';

UPDATE `logs` 
 SET `level`='Suggestion' WHERE `level_id`='4';

UPDATE `logs` 
 SET `level`='Urgent' WHERE `level_id`='5';

ALTER TABLE `logs`
 DROP COLUMN `level_id`, 
 DROP INDEX `level_id_fk`,
 DROP FOREIGN KEY `level_id_fk`;
