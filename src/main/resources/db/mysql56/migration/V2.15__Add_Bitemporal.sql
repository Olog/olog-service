
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