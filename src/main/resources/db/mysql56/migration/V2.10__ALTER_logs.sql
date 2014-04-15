ALTER TABLE `logs`
 ADD COLUMN `entry_id` INT(11) NOT NULL;


UPDATE `logs`
 SET `entry_id` = CASE WHEN `parent_id` IS NULL THEN `id` ELSE `parent_id` END;

INSERT INTO `entries` 
 (`id`, `created`) SELECT `id`, `created` FROM `logs` WHERE `parent_id` IS NULL;

ALTER TABLE `logs` 
 DROP COLUMN `parent_id`;

ALTER TABLE `logs` 
 CHANGE COLUMN `created` `modified` DATETIME NOT NULL,
 MODIFY COLUMN `entry_id` INT(11) UNSIGNED NOT NULL,
 ADD INDEX `entry_id_fk`(`entry_id`),
 ADD CONSTRAINT `entry_id_fk` FOREIGN KEY `entry_id_fk` (`entry_id`)
    REFERENCES `entries` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

