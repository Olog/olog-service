﻿
DROP TABLE IF EXISTS `olog`.`bitemporal_log`;
CREATE TABLE  `olog`.`bitemporal_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) unsigned DEFAULT NULL,
  `recordinterval` datetime DEFAULT NULL,
  `recordinterval_0` datetime DEFAULT NULL,
  `validityinterval` datetime DEFAULT NULL,
  `validityinterval_0` datetime DEFAULT NULL,
  `entry_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `logs` (`log_id`),
  KEY `entries` (`entry_id`) USING BTREE,
  CONSTRAINT `entries_FK2` FOREIGN KEY (`entry_id`) REFERENCES `entries` (`id`),
  CONSTRAINT `logs_FK1` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`)
);

ALTER TABLE `olog`.`entries` ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `created`;

INSERT INTO `olog`.`bitemporal_log` (
	`log_id`, 
	`recordinterval_0`, 
	`recordinterval`, 
	`validityinterval_0`, 
	`validityinterval`, 
	`entry_id`)
  SELECT `id`, 
         `modified`, 
	 if((SELECT `modified` FROM `olog`.`logs` e WHERE e.`id` > l.`id` and e.`entry_id`=l.`entry_id` ORDER BY `id` LIMIT 1) is null, 
		'3197-09-13 18:00:00', 
		(SELECT `modified` FROM `olog`.`logs` e WHERE e.`id` > l.`id` and e.`entry_id`=l.`entry_id` ORDER BY `id` LIMIT 1)), 
	`modified`, 
	'3197-09-13 18:00:00',
	`entry_id` 
	from `olog`.`logs` l;

ALTER TABLE `olog`.`bitemporal_log` ADD INDEX `record_0` USING BTREE(`recordinterval_0`),
 ADD INDEX `record` USING BTREE(`recordinterval`),
 ADD INDEX `valid_0` USING BTREE(`validityinterval_0`),
 ADD INDEX `valid` USING BTREE(`validityinterval`);

ALTER TABLE `olog`.`bitemporal_log` ADD INDEX `ids`(`log_id`, `entry_id`);

UPDATE `olog`.`bitemporal_log` SET recordinterval='3197-09-13 18:00:00' where recordinterval_0 > recordinterval;

ALTER TABLE `olog`.`logs_attributes` MODIFY COLUMN `grouping_num` INT(11) DEFAULT NULL;