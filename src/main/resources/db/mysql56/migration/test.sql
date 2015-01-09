
DROP TABLE IF EXISTS `olog`.`bitemporal_log`;
CREATE TABLE  `olog`.`bitemporal_log` (
  `id` INT(11) NOT NULL,
  `log_id` INT(11) UNSIGNED DEFAULT NULL,
  `recordinterval` datetime DEFAULT NULL,
  `recordinterval_0` datetime DEFAULT NULL,
  `validityinterval` datetime DEFAULT NULL,
  `validityinterval_0` datetime DEFAULT NULL,
  `entry_id` INT(11) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `logs` (`log_id`),
  KEY `entries` (`entry_id`),
  CONSTRAINT `logs_FK1` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`),
  CONSTRAINT `entries_FK2` FOREIGN KEY (`entry_id`) REFERENCES `entries` (`id`)
);

ALTER TABLE `olog`.`entries` ADD COLUMN `state` ENUM('Active','Inactive') NOT NULL DEFAULT 'Active' AFTER `created`;