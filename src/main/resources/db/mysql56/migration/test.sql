
DROP TABLE IF EXISTS `olog`.`bitemporal_log`;
CREATE TABLE  `olog`.`bitemporal_log` (
  `id` INT(11) NOT NULL,
  `log_id` INT(11) UNSIGNED DEFAULT NULL,
  `recordinterval` datetime DEFAULT NULL,
  `recordinterval_0` datetime DEFAULT NULL,
  `validityinterval` datetime DEFAULT NULL,
  `validityinterval_0` datetime DEFAULT NULL,
  `log` INT(11) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `logs` (`log_id`),
  KEY `entries` (`log`),
  CONSTRAINT `logs_FK1` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`),
  CONSTRAINT `entries_FK2` FOREIGN KEY (`log`) REFERENCES `entries` (`id`)
);
