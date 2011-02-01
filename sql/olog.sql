-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.41


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema ologdb4
--

CREATE DATABASE IF NOT EXISTS ologdb4;
USE ologdb4;

--
-- Definition of table `acos`
--

DROP TABLE IF EXISTS `acos`;
CREATE TABLE `acos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `foreign_key` int(11) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `lft` int(11) DEFAULT NULL,
  `rght` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `acos_parent_id_fk` (`parent_id`),
  CONSTRAINT `acos_parent_id_fk` FOREIGN KEY (`parent_id`) REFERENCES `acos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acos`
--

/*!40000 ALTER TABLE `acos` DISABLE KEYS */;
INSERT INTO `acos` (`id`,`parent_id`,`model`,`foreign_key`,`alias`,`lft`,`rght`) VALUES 
 (1,NULL,NULL,NULL,'controllers',1,162),
 (2,1,NULL,NULL,'Pages',2,17),
 (3,2,NULL,NULL,'display',3,4),
 (4,2,NULL,NULL,'build_acl',5,6),
 (5,2,NULL,NULL,'add',7,8),
 (6,2,NULL,NULL,'edit',9,10),
 (7,2,NULL,NULL,'index',11,12),
 (8,2,NULL,NULL,'view',13,14),
 (9,2,NULL,NULL,'delete',15,16),
 (10,1,NULL,NULL,'Groups',18,31),
 (11,10,NULL,NULL,'index',19,20),
 (12,10,NULL,NULL,'view',21,22),
 (13,10,NULL,NULL,'add',23,24),
 (14,10,NULL,NULL,'edit',25,26),
 (15,10,NULL,NULL,'delete',27,28),
 (16,10,NULL,NULL,'build_acl',29,30),
 (17,1,NULL,NULL,'Levels',32,45),
 (18,17,NULL,NULL,'index',33,34),
 (19,17,NULL,NULL,'view',35,36),
 (20,17,NULL,NULL,'add',37,38),
 (21,17,NULL,NULL,'edit',39,40),
 (22,17,NULL,NULL,'delete',41,42),
 (23,17,NULL,NULL,'build_acl',43,44),
 (24,1,NULL,NULL,'Logs',46,59),
 (25,24,NULL,NULL,'index',47,48),
 (26,24,NULL,NULL,'view',49,50),
 (27,24,NULL,NULL,'add',51,52),
 (28,24,NULL,NULL,'edit',53,54),
 (29,24,NULL,NULL,'delete',55,56),
 (30,24,NULL,NULL,'build_acl',57,58),
 (31,1,NULL,NULL,'LogsTags',60,73),
 (32,31,NULL,NULL,'index',61,62),
 (33,31,NULL,NULL,'view',63,64),
 (34,31,NULL,NULL,'add',65,66),
 (35,31,NULL,NULL,'edit',67,68),
 (36,31,NULL,NULL,'delete',69,70),
 (37,31,NULL,NULL,'build_acl',71,72),
 (38,1,NULL,NULL,'Properties',74,87),
 (39,38,NULL,NULL,'index',75,76),
 (40,38,NULL,NULL,'view',77,78),
 (41,38,NULL,NULL,'add',79,80),
 (42,38,NULL,NULL,'edit',81,82),
 (43,38,NULL,NULL,'delete',83,84),
 (44,38,NULL,NULL,'build_acl',85,86),
 (45,1,NULL,NULL,'Subscriptions',88,101),
 (46,45,NULL,NULL,'index',89,90),
 (47,45,NULL,NULL,'view',91,92),
 (48,45,NULL,NULL,'add',93,94),
 (49,45,NULL,NULL,'edit',95,96),
 (50,45,NULL,NULL,'delete',97,98),
 (51,45,NULL,NULL,'build_acl',99,100),
 (52,1,NULL,NULL,'Tags',102,115),
 (53,52,NULL,NULL,'index',103,104),
 (54,52,NULL,NULL,'view',105,106),
 (55,52,NULL,NULL,'add',107,108),
 (56,52,NULL,NULL,'edit',109,110),
 (57,52,NULL,NULL,'delete',111,112),
 (58,52,NULL,NULL,'build_acl',113,114),
 (59,1,NULL,NULL,'Uploads',116,129),
 (60,59,NULL,NULL,'index',117,118),
 (61,59,NULL,NULL,'view',119,120),
 (62,59,NULL,NULL,'add',121,122),
 (63,59,NULL,NULL,'edit',123,124),
 (64,59,NULL,NULL,'delete',125,126),
 (65,59,NULL,NULL,'build_acl',127,128),
 (66,1,NULL,NULL,'Users',130,147),
 (67,66,NULL,NULL,'login',131,132),
 (68,66,NULL,NULL,'logout',133,134),
 (69,66,NULL,NULL,'index',135,136),
 (70,66,NULL,NULL,'view',137,138),
 (71,66,NULL,NULL,'add',139,140),
 (72,66,NULL,NULL,'edit',141,142),
 (73,66,NULL,NULL,'delete',143,144),
 (74,66,NULL,NULL,'build_acl',145,146),
 (75,1,NULL,NULL,'Wikis',148,161),
 (76,75,NULL,NULL,'index',149,150),
 (77,75,NULL,NULL,'view',151,152),
 (78,75,NULL,NULL,'add',153,154),
 (79,75,NULL,NULL,'edit',155,156),
 (80,75,NULL,NULL,'delete',157,158),
 (81,75,NULL,NULL,'build_acl',159,160);
/*!40000 ALTER TABLE `acos` ENABLE KEYS */;


--
-- Definition of table `aros`
--

DROP TABLE IF EXISTS `aros`;
CREATE TABLE `aros` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `foreign_key` int(11) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `lft` int(11) DEFAULT NULL,
  `rght` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `aros_parent_id_fk` (`parent_id`),
  CONSTRAINT `aros_parent_id_fk` FOREIGN KEY (`parent_id`) REFERENCES `aros` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `aros`
--

/*!40000 ALTER TABLE `aros` DISABLE KEYS */;
INSERT INTO `aros` (`id`,`parent_id`,`model`,`foreign_key`,`alias`,`lft`,`rght`) VALUES 
 (3,NULL,'Group',1,NULL,1,6),
 (4,NULL,'Group',2,NULL,7,8),
 (5,NULL,'Group',3,NULL,9,16),
 (6,3,'User',3,NULL,17,18),
 (7,5,'User',4,NULL,10,11),
 (8,3,'User',5,NULL,4,5);
/*!40000 ALTER TABLE `aros` ENABLE KEYS */;


--
-- Definition of table `aros_acos`
--

DROP TABLE IF EXISTS `aros_acos`;
CREATE TABLE `aros_acos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aro_id` int(11) NOT NULL,
  `aco_id` int(11) NOT NULL,
  `_create` varchar(2) NOT NULL DEFAULT '0',
  `_read` varchar(2) NOT NULL DEFAULT '0',
  `_update` varchar(2) NOT NULL DEFAULT '0',
  `_delete` varchar(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ARO_ACO_KEY` (`aro_id`,`aco_id`),
  KEY `aro_id_fk` (`aro_id`),
  KEY `aco_id_fk` (`aco_id`),
  CONSTRAINT `aco_id_fk` FOREIGN KEY (`aco_id`) REFERENCES `acos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `aro_id_fk` FOREIGN KEY (`aro_id`) REFERENCES `aros` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `aros_acos`
--

/*!40000 ALTER TABLE `aros_acos` DISABLE KEYS */;
INSERT INTO `aros_acos` (`id`,`aro_id`,`aco_id`,`_create`,`_read`,`_update`,`_delete`) VALUES 
 (1,3,1,'1','1','1','1'),
 (2,4,1,'-1','-1','-1','-1'),
 (3,4,24,'1','1','1','1'),
 (4,4,10,'1','1','1','1'),
 (5,4,52,'1','1','1','1'),
 (6,5,1,'-1','-1','-1','-1'),
 (7,5,27,'1','1','1','1'),
 (8,5,28,'1','1','1','1');
/*!40000 ALTER TABLE `aros_acos` ENABLE KEYS */;


--
-- Definition of table `groups`
--

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `groups`
--

/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` (`id`,`name`,`created`,`modified`) VALUES 
 (1,'administrators','2010-06-07 14:07:03','2010-06-07 14:07:03'),
 (2,'managers','2010-06-07 14:08:00','2010-06-07 14:08:00'),
 (3,'users','2010-06-07 14:08:08','2010-06-07 14:08:08');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;


--
-- Definition of table `levels`
--

DROP TABLE IF EXISTS `levels`;
CREATE TABLE `levels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `levels`
--

/*!40000 ALTER TABLE `levels` DISABLE KEYS */;
INSERT INTO `levels` (`id`,`name`) VALUES 
 (1,'Info'),
 (4,'Problem'),
 (3,'Request'),
 (2,'Suggestion'),
 (5,'Urgent');
/*!40000 ALTER TABLE `levels` ENABLE KEYS */;


--
-- Definition of table `logbooks`
--

DROP TABLE IF EXISTS `logbooks`;
CREATE TABLE `logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `is_tag` int(1) NOT NULL DEFAULT '0',
  `owner` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logbooks`
--

/*!40000 ALTER TABLE `logbooks` DISABLE KEYS */;
INSERT INTO `logbooks` (`id`,`name`,`is_tag`,`owner`) VALUES 
 (1,'Operations',0,NULL),
 (2,'Electronics Maintenance',0,NULL),
 (3,'Mechanical Technicians',0,NULL),
 (4,'LOTO',0,NULL),
 (5,'Inverpower Power Supplies',1,NULL),
 (6,'RF Area',1,NULL),
 (7,'Kicker',1,NULL),
 (8,'Bumps',1,NULL),
 (9,'Septums',1,NULL),
 (10,'Large Power Supplies',1,NULL),
 (11,'Timing Systems',1,NULL);
/*!40000 ALTER TABLE `logbooks` ENABLE KEYS */;


--
-- Definition of table `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `source` varchar(80) NOT NULL DEFAULT '',
  `owner` varchar(32) NOT NULL,
  `level_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL DEFAULT '1',
  `subject` varchar(75) NOT NULL DEFAULT '',
  `description` mediumtext NOT NULL,
  `md5entry` varchar(32) NOT NULL DEFAULT '',
  `md5recent` mediumtext NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `level_id_fk` (`level_id`),
  KEY `log_parent_id_fk` (`parent_id`),
  KEY `status_id_fk` (`status_id`),
  CONSTRAINT `level_id_fk` FOREIGN KEY (`level_id`) REFERENCES `levels` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `log_parent_id_fk` FOREIGN KEY (`parent_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `status_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs`
--

/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
INSERT INTO `logs` (`id`,`created`,`modified`,`source`,`owner`,`level_id`,`status_id`,`subject`,`description`,`md5entry`,`md5recent`,`parent_id`) VALUES 
 (2,'2010-12-20 09:43:38','2010-12-20 09:43:38','::1','5',1,3,'test','<p>test</p>','','',NULL),
 (3,'2010-12-20 09:47:11','2010-12-20 09:47:11','::1','5',1,2,'test','<p>test</p>','','',3),
 (4,'2010-12-20 09:47:11','2010-12-20 09:47:26','::1','5',1,1,'test','<p>test2</p>','5702d9ca11f0d14c7ff09b8395a850a0','',3),
 (5,'2010-12-20 10:12:53','2010-12-20 10:12:53','::1','4',1,3,'adfs','<p>awef</p>','','',NULL);
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;


--
-- Definition of table `logs_logbooks`
--

DROP TABLE IF EXISTS `logs_logbooks`;
CREATE TABLE `logs_logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `logbook_id` int(11) NOT NULL,
  `status` enum('open','closed') DEFAULT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `log_id_fk` (`log_id`),
  KEY `logbook_id_fk` (`logbook_id`) USING BTREE,
  CONSTRAINT `logbook_id_fk` FOREIGN KEY (`logbook_id`) REFERENCES `logbooks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs_logbooks`
--

/*!40000 ALTER TABLE `logs_logbooks` DISABLE KEYS */;
INSERT INTO `logs_logbooks` (`id`,`log_id`,`logbook_id`,`status`,`created`) VALUES 
 (12,2,7,NULL,'0000-00-00 00:00:00'),
 (13,2,8,NULL,'0000-00-00 00:00:00'),
 (14,2,2,NULL,'0000-00-00 00:00:00'),
 (15,3,7,NULL,'0000-00-00 00:00:00'),
 (16,3,8,NULL,'0000-00-00 00:00:00'),
 (17,3,9,NULL,'0000-00-00 00:00:00'),
 (18,3,10,NULL,'0000-00-00 00:00:00'),
 (19,3,1,NULL,'0000-00-00 00:00:00'),
 (20,4,7,NULL,'0000-00-00 00:00:00'),
 (21,4,8,NULL,'0000-00-00 00:00:00'),
 (22,4,9,NULL,'0000-00-00 00:00:00'),
 (23,4,10,NULL,'0000-00-00 00:00:00'),
 (24,4,1,NULL,'0000-00-00 00:00:00'),
 (25,5,2,NULL,'0000-00-00 00:00:00');
/*!40000 ALTER TABLE `logs_logbooks` ENABLE KEYS */;


--
-- Definition of table `properties`
--

DROP TABLE IF EXISTS `properties`;
CREATE TABLE `properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `properties_log_id_fk` (`log_id`),
  CONSTRAINT `_properties_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `properties`
--

/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;


--
-- Definition of table `rest_logs`
--

DROP TABLE IF EXISTS `rest_logs`;
CREATE TABLE `rest_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `class` varchar(40) NOT NULL,
  `username` varchar(40) NOT NULL,
  `controller` varchar(40) NOT NULL,
  `action` varchar(40) NOT NULL,
  `model_id` varchar(40) NOT NULL,
  `ip` varchar(16) NOT NULL,
  `requested` datetime NOT NULL,
  `apikey` varchar(64) NOT NULL,
  `httpcode` smallint(3) unsigned NOT NULL,
  `error` varchar(255) NOT NULL,
  `ratelimited` tinyint(1) unsigned NOT NULL,
  `data_in` text NOT NULL,
  `meta` text NOT NULL,
  `data_out` text NOT NULL,
  `responded` datetime NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rest_logs`
--

/*!40000 ALTER TABLE `rest_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `rest_logs` ENABLE KEYS */;


--
-- Definition of table `statuses`
--

DROP TABLE IF EXISTS `statuses`;
CREATE TABLE `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `statuses`
--

/*!40000 ALTER TABLE `statuses` DISABLE KEYS */;
INSERT INTO `statuses` (`id`,`name`) VALUES 
 (1,'Active'),
 (2,'edit'),
 (3,'Inactive');
/*!40000 ALTER TABLE `statuses` ENABLE KEYS */;


--
-- Definition of table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
CREATE TABLE `subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL,
  `email` varchar(250) NOT NULL DEFAULT '',
  `webhook` varchar(250) DEFAULT NULL,
  `level_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptions_tag_id_fk` (`tag_id`),
  CONSTRAINT `subscriptions_tag_id_fk` FOREIGN KEY (`tag_id`) REFERENCES `logbooks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subscriptions`
--

/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;


--
-- Definition of table `uploads`
--

DROP TABLE IF EXISTS `uploads`;
CREATE TABLE `uploads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `store` varchar(200) NOT NULL,
  `type` varchar(200) NOT NULL,
  `size` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `uploads_log_id_fk` (`log_id`),
  CONSTRAINT `uploads_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `uploads`
--

/*!40000 ALTER TABLE `uploads` DISABLE KEYS */;
INSERT INTO `uploads` (`id`,`log_id`,`name`,`store`,`type`,`size`,`created`,`modified`) VALUES 
 (2,5,'test.txt','5\\test.txt','text/plain',553,'2010-12-20 10:12:53','2010-12-20 10:12:53');
/*!40000 ALTER TABLE `uploads` ENABLE KEYS */;


--
-- Definition of table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` char(40) NOT NULL,
  `name` char(40) NOT NULL,
  `group_id` int(11) NOT NULL,
  `active` tinyint(4) NOT NULL DEFAULT '0',
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `group_id_fk` (`group_id`),
  CONSTRAINT `group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`,`username`,`password`,`name`,`group_id`,`active`,`created`,`modified`) VALUES 
 (3,'admin','b2a3a33b9fc09d116a1cc4e273349a4a72870481','admin',1,0,'2010-06-07 14:08:42','2010-12-20 09:59:52'),
 (4,'test','b2a3a33b9fc09d116a1cc4e273349a4a72870481','test',3,0,'2010-06-07 14:29:57','2010-12-20 10:05:33'),
 (5,'berryman','72067ef8f1088b382ed8b0d5e0017ecb7b46cc0e','Eric Berryman',1,0,'2010-12-20 09:43:30','2011-01-13 08:21:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;


--
-- Definition of table `wikis`
--

DROP TABLE IF EXISTS `wikis`;
CREATE TABLE `wikis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `wiki` varchar(45) NOT NULL,
  `author` varchar(45) NOT NULL,
  `url` varchar(45) NOT NULL,
  `history` varchar(45) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `wikis_log_id_fk` (`log_id`),
  CONSTRAINT `wikis_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `wikis`
--

/*!40000 ALTER TABLE `wikis` DISABLE KEYS */;
/*!40000 ALTER TABLE `wikis` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
