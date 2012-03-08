#drop keys / index
ALTER TABLE `properties` DROP INDEX `properties_log_id_fk`,
DROP FOREIGN KEY `properties_log_id_fk`;

#add attributes table:
DROP TABLE IF EXISTS `attributes`;
CREATE TABLE  `attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `property_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `status_id` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attributes_property_id_fk` (`property_id`),
  CONSTRAINT `attributes_property_id_fk` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=InnoDB;

#add logs_attributes table:
DROP TABLE IF EXISTS `logs_attributes`;
CREATE TABLE  `logs_attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `attribute_id` int(11) NOT NULL,
  `value` varchar(200) NOT NULL,
  `grouping_num` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `logs_attributes_attribute_id_fk` (`attribute_id`),
  KEY `logs_attributes_log_id_fk` (`log_id`),
  CONSTRAINT `logs_attributes_attribute_id_fk` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `logs_attributes_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

#drop value column on property table:
ALTER TABLE `properties` DROP COLUMN `value`;

#drop log_id column on property table:
ALTER TABLE `properties` DROP COLUMN `log_id`;

#add status_id column to proprty table
ALTER TABLE `properties` ADD `status_id` tinyint(1) NOT NULL;

#Delete old example property entry
DELETE FROM `properties` WHERE `id`=1;
DELETE FROM `properties` WHERE `id`=2;
DELETE FROM `properties` WHERE `id`=3;
DELETE FROM `properties` WHERE `id`=4;

#drop subject column on logs table:
ALTER TABLE `logs` DROP COLUMN `subject`;

#fix typo
UPDATE `logbooks` SET `name`='REA Maintenance' WHERE `name`='ReA Maintenace';
